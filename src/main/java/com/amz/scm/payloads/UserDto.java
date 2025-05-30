package com.amz.scm.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amz.scm.models.Providers;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotEmpty
    @Size(min = 3, message = "Full name must be at least 3 characters")
    private String fullName;
    @NotEmpty
    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;
    @NotEmpty

    @Email(message = "Email address is not valid")
    private String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;


    private String phoneNumber;
    private String address;
    private String about;

    private boolean isEnabled; //set at runtime
    private Date createdAt; //set at runtime
    private Providers provider; //set at runtime
    
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // private List<String> roleList = new ArrayList<>();

    private Set<RoleDto> roles = new HashSet<>();
}
