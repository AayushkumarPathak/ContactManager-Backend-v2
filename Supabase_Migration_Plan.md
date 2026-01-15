# Supabase Migration: High-Level Plan

This document outlines the high-level strategy for migrating the image upload functionality from AWS S3 to Supabase Storage.

## 1. Environment Setup

*   **Supabase Project:** Create a new project in the Supabase dashboard.
*   **Storage Bucket:** Create a new storage bucket (e.g., `contact-pictures`) with appropriate access policies (public read access for images).
*   **Dependencies:** Add the Supabase storage client library to the `pom.xml`.

## 2. Implementation Phase

*   **Configuration:**
    *   Create a `SupabaseConfig.java` to initialize the Supabase storage client as a Spring bean.
    *   Add Supabase credentials (URL, API keys) to the `application.properties` file.
*   **Service Implementation:**
    *   Create `SupabaseImageUploaderService.java` that implements the existing `ImageUploader` interface.
    *   Implement the `uploadImage` and `deleteImage` methods using the Supabase client.
*   **Integration:**
    *   Update the dependency injection in `ContactServiceImpl` to use the new `SupabaseImageUploaderService`. The `@Primary` annotation can be used for a seamless switch.
*   **Refactoring:**
    *   Move the image deletion logic from `ContactController` to `ContactServiceImpl` to improve separation of concerns and reduce coupling.

## 3. Testing

*   **Unit Tests:** Create unit tests for the `SupabaseImageUploaderService` to verify upload and delete functionality (if possible, with a mock Supabase client).
*   **Integration Tests:** Thoroughly test the entire contact creation, update, and deletion flows to ensure images are handled correctly.

## 4. Deployment & Cleanup

*   **Deploy:** Deploy the updated application.
*   **Cleanup:** After verifying the new implementation in production, remove the old `S3ImageUploaderService`, `S3Config`, and the AWS S3 dependency from `pom.xml`.
