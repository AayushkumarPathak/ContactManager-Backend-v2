package com.amz.scm.services.impl;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amz.scm.exceptions.ImageUploadException;
import com.amz.scm.services.ImageUploader;

public class S3ImageUploaderService implements ImageUploader {

    @Autowired
    private AmazonS3 client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadImage(MultipartFile imageFile) {

        if (imageFile == null) {
            throw new ImageUploadException("S3ImageUploader: Error uploading image imageFile is null");
        }

        String originalImageFileName = imageFile.getOriginalFilename();

        if (originalImageFileName == null)
            throw new ImageUploadException("S3ImageUploader: originalImageFileName is NULL!!");

        String customImageFileName = UUID.randomUUID().toString()
                + originalImageFileName.substring(originalImageFileName.lastIndexOf("."));

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageFile.getSize());

        try {
            PutObjectResult putObjectResult = client.putObject(
                    new PutObjectRequest(bucketName, customImageFileName, imageFile.getInputStream(), metadata));

            return this.preSignedUrl(customImageFileName);

        } catch (IOException e) {
            throw new ImageUploadException("S3ImageUploader: Error in uploading image to S3 " + e.getMessage());
        }

    }

    @Override
    public String preSignedUrl(String imageFileName) {

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
                imageFileName).withMethod(HttpMethod.GET);

        URL imageS3Url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return imageFileName.toString();
    }

}
