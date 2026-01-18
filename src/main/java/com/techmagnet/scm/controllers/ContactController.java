package com.techmagnet.scm.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.techmagnet.scm.apiResponses.ApiResponseEntity;
import com.techmagnet.scm.exceptions.ApiException;
import com.techmagnet.scm.utils.AppConstants;
import com.techmagnet.scm.models.User;
import com.techmagnet.scm.payloads.ContactDto;
import com.techmagnet.scm.payloads.ContactResponse;
import com.techmagnet.scm.services.ContactService;
import com.techmagnet.scm.services.ImageUploader;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Rest controller for handling contact api's
 */
@RestController
@RequestMapping("/api/v2/contact")
public class ContactController {

    private final ContactService contactService;
    private final ImageUploader imageUploader;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ContactController(ContactService contactService, ImageUploader imageUploader) {
        this.contactService = contactService;
        this.imageUploader = imageUploader;
    }


    /**
     * Validate users for their access to the operations
     *
     * @param requestedUserId
     */
    private void validateUserAccess(Long requestedUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getId() != requestedUserId) {
            throw new ApiException("Access denied. You can only access your own contacts.");
        }
    }

    /**
     * Api to test the infra setup
     *
     * @return
     */
    @GetMapping("/test")
    public ResponseEntity<?> testApi() {
        ApiResponseEntity<?> response = new ApiResponseEntity<>(null,
                true, "Contact Api is working fine", null, 200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Api to create contact on user
     *
     * @param contactDtoStr
     * @param imageFile
     * @param userid
     * @return
     */
    @PostMapping(value = "/{userid}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseEntity<?>> createContact(
            @RequestParam("contactDto") String contactDtoStr,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @PathVariable Long userid) {

        // Validate user access
        validateUserAccess(userid);


        ContactDto contactDto;
        try {

            contactDto = objectMapper.readValue(contactDtoStr, ContactDto.class);

            boolean isContactExistsWithFullname = this.contactService.checkContactExistsWithFullname(contactDto.getFullName(), userid);

            if (isContactExistsWithFullname) {
                return new ResponseEntity<>(
                        new ApiResponseEntity<>(null, false, "Contact with given fullName  already exists Use Unique Name", null, 409),
                        HttpStatus.CONFLICT
                );
            }

            ContactDto savedContact = this.contactService.createContact(contactDto, userid, imageFile);
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(savedContact, true, "Contact created successfully", null, 200),
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(null, false, "Unable to save contact",
                            e.getMessage(), 400), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Api to fetch contact by id;
     *
     * @param contactId
     * @return
     */
    @GetMapping("/{contactId}")
    public ResponseEntity<ApiResponseEntity<?>> getContactById(@PathVariable Long contactId) {
        ContactDto contact = this.contactService.getContactById(contactId);

        // Get the user ID associated with this contact and validate access
        Long contactUserId = contact.getUser().getId();
        validateUserAccess(contactUserId);

        return new ResponseEntity<>(
                new ApiResponseEntity<>(contact, true, "Contact fetched successfully", null, 200),
                HttpStatus.OK);
    }

    /**
     * Api to fetch all the contact created by particular user
     *
     * @param uid
     * @param pageNumber
     * @param pageSize
     * @param sortBy
     * @param sortDir
     * @return
     */
    @GetMapping("/user/{uid}")
    public ResponseEntity<ApiResponseEntity<?>> getContactsByUser(
            @PathVariable Long uid,
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

        // Validate user access
        validateUserAccess(uid);

        try {
            ContactResponse allContactsByUser = this.contactService.getAllContactsByUser(uid, pageNumber, pageSize, sortBy, sortDir);

            ApiResponseEntity<ContactResponse> apiResponseEntity = new ApiResponseEntity<>();
            apiResponseEntity.setData(allContactsByUser);
            apiResponseEntity.setStatusCode(200);
            apiResponseEntity.setSuccess(true);
            apiResponseEntity.setErrors(null);
            apiResponseEntity.setMessage("Contacts Fetched for user with uid " + uid);

            return new ResponseEntity<ApiResponseEntity<?>>(apiResponseEntity, HttpStatus.OK);

        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }


    /**
     * Api to delete contact by id
     *
     * @param contactId
     * @return
     */
    @DeleteMapping("/{contactId}")
    public ResponseEntity<ApiResponseEntity<?>> deleteContact(@PathVariable Long contactId) {
        // Fetch the contact to get the user ID for access validation
        ContactDto contact = this.contactService.getContactById(contactId);
        Long contactUserId = contact.getUser().getId();
        validateUserAccess(contactUserId);

        try {
            String pictureUrl = contact.getPicture();
            if (pictureUrl != null && pictureUrl.contains("/storage/v1/object/")) {
                String fileName = pictureUrl.substring(pictureUrl.lastIndexOf("/") + 1);
                imageUploader.deleteImage(fileName);
            }
            this.contactService.deleteContact(contactId);
            ApiResponseEntity<?> response = new ApiResponseEntity<>(
                    null,
                    true,
                    "Contact deleted successfully",
                    null,
                    200
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(null, false, "Unable to delete contact", e.getMessage(), 400),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Api to update contact details
     * @param contactId
     * @param contactDto
     * @return
     */
    @PutMapping("/{contactId}")
    public ResponseEntity<ApiResponseEntity<?>> updateContact(@PathVariable Long contactId, @RequestBody ContactDto contactDto) {

        try {
            ContactDto updatedContact = this.contactService.updateContact(contactId, contactDto);


            return new ResponseEntity<>(
                    new ApiResponseEntity<>(updatedContact, true, "Contact created successfully", null, 200),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponseEntity<String>(null, false, "Failed to update contact. Error occured", e.getMessage(), 400),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

}
