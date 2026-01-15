# Supabase Migration: In-Depth Implementation Guide

This guide provides a detailed walkthrough for migrating from AWS S3 to Supabase Storage using direct REST API calls with Spring's `RestTemplate`.

## 1. Supabase Console Setup

1.  **Create a Project:**
    *   Go to the [Supabase Dashboard](https://app.supabase.io/) and create a new project.
2.  **Create a Storage Bucket:**
    *   In your Supabase project, navigate to the "Storage" section.
    *   Click "New bucket".
    *   Enter a name for your bucket, for example, `contact-pictures`.
    *   For "Bucket type," select "Public". This will make the files publicly accessible for reading.
3.  **Get API Keys:**
    *   Go to "Project Settings" -> "API".
    *   You will need the following:
        *   **Project URL** (under "Configuration" -> "URL")
        *   **`service_role` `secret` key** (under "Project API keys")

## 2. Project Configuration

### 2.1. Update `application.properties`

Add your Supabase credentials to `src/main/resources/application.properties`:

```properties
# Supabase
supabase.url=YOUR_SUPABASE_URL
supabase.service-key=YOUR_SUPABASE_SERVICE_ROLE_KEY
```
**Important:** Do not commit your service key to a public repository. Use environment variables or a secret management tool for production.

### 2.2. Create `RestTemplate` Bean

Create a configuration class to provide a `RestTemplate` bean.

**File:** `src/main/java/com/amz/scm/configs/AppConfig.java`

```java
package com.techmagnet.scm.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

## 3. Java Implementation

### 3.1. Implement `SupabaseImageUploaderService.java`

Create the Supabase implementation of the `ImageUploader` interface using `RestTemplate`.

**File:** `src/main/java/com/amz/scm/services/impl/SupabaseImageUploaderService.java`

```java
package com.techmagnet.scm.services.impl;

import com.techmagnet.scm.services.ImageUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Primary
public class SupabaseImageUploaderService implements ImageUploader {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String supabaseServiceKey;

    private static final String BUCKET_NAME = "contact-pictures";

    @Override
    public String uploadImage(MultipartFile image, String fileName) throws IOException {
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseServiceKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

        return supabaseUrl + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
    }

    @Override
    public void deleteImage(String fileName) {
        String deleteUrl = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseServiceKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, Void.class);
    }
}
```

### 3.2. Refactor `ContactController` and `ContactServiceImpl`

This part of the plan remains the same. We will move the deletion logic to the service layer.

*   **Follow the instructions in the `Supabase_Migration_Implementation_Guide.md` for refactoring `ContactController` and `ContactServiceImpl`** as they were previously written. The logic for these two classes does not change with the move to `RestTemplate`.

## 4. Testing and Cleanup

*   **Testing and cleanup instructions remain the same as in the previous version of this guide.**

This approach requires no new dependencies and uses standard Spring components.
