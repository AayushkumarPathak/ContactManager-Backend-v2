package com.amz.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amz.scm.apiResponses.ApiResponseEntity;
import com.amz.scm.exceptions.ApiException;
import com.amz.scm.helpers.AppConstants;
import com.amz.scm.models.User;
import com.amz.scm.payloads.ContactDto;
import com.amz.scm.payloads.ContactResponse;
import com.amz.scm.services.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v2/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Value("${project.image}")
    private String path;

    private void validateUserAccess(Long requestedUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getId() != requestedUserId) {
            throw new ApiException("Access denied. You can only access your own contacts.");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testApi() {
        ApiResponseEntity<?> response = new ApiResponseEntity<>(null,
            true, "Contact Api is working fine", null, 200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{userid}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseEntity<?>> createContact(
        @RequestParam("contactDto") String contactDtoStr,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        @PathVariable Long userid) {
        
        // Validate user access
        validateUserAccess(userid);

        

        ObjectMapper objectMapper = new ObjectMapper();
        ContactDto contactDto;
        try {

            contactDto = objectMapper.readValue(contactDtoStr, ContactDto.class);

            boolean isContactExistsWithFullname = this.contactService.checkContactExistsWithFullname(contactDto.getFullName());

             if(isContactExistsWithFullname){
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
            return new ResponseEntity<>(
                new ApiResponseEntity<>(null, false, "Unable to save contact",
                    e.getMessage(), 400), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
}
