package com.amz.scm.services;



import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {

    String uploadImage(MultipartFile imageFile);

    String preSignedUrl(String imageFileName);

}
