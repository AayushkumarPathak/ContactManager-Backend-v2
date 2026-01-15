package com.techmagnet.scm.services.impl;

import com.techmagnet.scm.services.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class SupabaseImageUploadServiceImpl implements ImageUploader {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.api.key}")
    private String supabaseApiKey;

    @Value("${supabase.bucket}")
    private String bucketName;


    @Override
    public String uploadImage(MultipartFile imageFile){
        try {
            String originalName = imageFile.getOriginalFilename().toLowerCase();
            String extension = originalName.substring(originalName.lastIndexOf("."));

            String fileName =
                    System.currentTimeMillis() + "-" + Math.abs(originalName.hashCode()) + extension;

            String uploadUrl =
                    supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(supabaseApiKey);
            headers.set("apikey", supabaseApiKey);
            headers.setContentType(MediaType.parseMediaType(imageFile.getContentType()));

            HttpEntity<byte[]> requestEntity =
                    new HttpEntity<>(imageFile.getBytes(), headers);

            restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;

        } catch (IOException e) {
            log.error("IOException while uploading image to Supabase", e);
            throw new RuntimeException("IOException ImageUploadException", e);

        } catch (Exception e) {
            log.error("Unexpected error while uploading image to Supabase", e);
            throw new RuntimeException("ImageUploadException", e);
        }
    }

    @Override
    public void deleteImage(String imageFileName) {
        try {
            String deleteUrl =
                    supabaseUrl + "/storage/v1/object/" + bucketName + "/" + imageFileName;

            log.debug("imageDeleteUrl: {}", deleteUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(supabaseApiKey);
            headers.set("apikey", supabaseApiKey);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
            );

            log.info("Image deleted successfully from Supabase: {}", imageFileName);

        } catch (Exception e) {
            log.error("Failed to delete image from Supabase: {}", imageFileName, e);
            throw new RuntimeException("ImageDeleteException", e);
        }
    }

}
