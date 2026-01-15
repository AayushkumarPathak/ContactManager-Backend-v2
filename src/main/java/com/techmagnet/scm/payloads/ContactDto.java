package com.techmagnet.scm.payloads;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;


import com.techmagnet.scm.models.SocialLink;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactDto {

    private long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters")
    private String fullName;

    @Email(message = "Email address is not valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    
    private String picture;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;


    private boolean favorite = false;
    private String websiteLink;
    private String linkedInLink;


    private Date createdAt;

    private UserDto user;

    private List<SocialLink> links = new ArrayList<>();

    
}
