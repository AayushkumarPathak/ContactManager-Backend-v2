package com.amz.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amz.scm.apiResponses.ApiResponseEntity;
import com.amz.scm.exceptions.ApiException;
import com.amz.scm.helpers.AppConstants;
import com.amz.scm.payloads.ContactDto;
import com.amz.scm.payloads.ContactResponse;
import com.amz.scm.services.ContactService;

@RestController
@RequestMapping("/api/v2/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/test")
    public ResponseEntity<?> testApi(){
        ApiResponseEntity<?> response = new ApiResponseEntity<>(null,
            true,"Contact Api is working fine",null,200
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/{userid}")
    public ResponseEntity<ApiResponseEntity<?>> createContact(@RequestBody ContactDto contact,@PathVariable Long userid){
        try {
            ContactDto savedContact = this.contactService.createContact(contact, userid);
            return new ResponseEntity<>(
                new ApiResponseEntity<>(savedContact, true, "Contact created successfully", null, 200),
                HttpStatus.OK
            );
        } catch (Exception e) {
           return new ResponseEntity<>(
            new ApiResponseEntity<>(null,false,"Unable to save user",
            e.getMessage(),400),HttpStatus.INTERNAL_SERVER_ERROR
           );
        }
    }
    
    @GetMapping("/{contactId}")
    public ResponseEntity<ApiResponseEntity<?>> getContactById(@PathVariable Long contactId){
        ContactDto contact = this.contactService.getContactById(contactId);
        return new ResponseEntity<>(
            new ApiResponseEntity<>(contact, true, "Contact fetched successfully", null, 200),
            HttpStatus.OK
        );
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<ApiResponseEntity<?>> getContactsByUser(@PathVariable Long uid,
        @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
        @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false)String sortBy,
        @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
    ){
        try {
            ContactResponse allContactsByUser = this.contactService.getAllContactsByUser(uid, pageNumber, pageSize, sortBy, sortDir);
    
            ApiResponseEntity<ContactResponse> apiResponseEntity = new ApiResponseEntity<>();
            apiResponseEntity.setData(allContactsByUser);
            apiResponseEntity.setStatusCode(200);
            apiResponseEntity.setSuccess(true);
            apiResponseEntity.setErrors(null);
            apiResponseEntity.setMessage("Contacts Fetched for user with uid"+uid);
    
            return new ResponseEntity<ApiResponseEntity<?>>(apiResponseEntity, HttpStatus.OK);
    
        } catch (Exception e) {
          throw new ApiException(e.getMessage());
        }
    }

    //search by email,name, phoneNumber

    //update

    //delete
    
    
}
