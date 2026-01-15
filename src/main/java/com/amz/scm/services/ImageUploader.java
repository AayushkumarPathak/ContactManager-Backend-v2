package com.techmagnet.scm.services;



import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {

    String uploadImage(MultipartFile imageFile);

    void deleteImage(String imageFileName);

}
