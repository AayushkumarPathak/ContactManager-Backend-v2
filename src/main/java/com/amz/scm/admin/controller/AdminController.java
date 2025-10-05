package com.amz.scm.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amz.scm.apiResponses.ApiResponseEntity;
import com.amz.scm.services.ImageUploader;

@RestController
@RequestMapping("/api/v2/admin")
public class AdminController {

    @Autowired
    private ImageUploader s3ServiceClient;


    @GetMapping("/testapi")
    public ResponseEntity<?> testAdminApi(){

        ApiResponseEntity<String> responseEntity = new ApiResponseEntity<String>("",true,"Admin Api working",null,200);
        
        
        return ResponseEntity.ok(responseEntity);
    }


    @DeleteMapping("/deleteObj")
    public ResponseEntity<?> deleteImageFromS3(@RequestParam String imageFileName){

        try {
        this.s3ServiceClient.deleteImage(imageFileName);

        ApiResponseEntity<String> responseEntity = new ApiResponseEntity<>(
            "",                         
            true,                      
            "Image deleted successfully from S3", 
            null,                      
            200                        
        );

        return ResponseEntity.ok(responseEntity);

    } catch (Exception e) {
        ApiResponseEntity<String> errorResponse = new ApiResponseEntity<>(
            null,
            false,
            "Failed to delete image from S3",
            e.getMessage(),
            500
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
        
    }



}
