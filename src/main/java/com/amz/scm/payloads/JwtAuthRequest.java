package com.amz.scm.payloads;

import lombok.Data;

@Data
public class JwtAuthRequest {
    private String username; // This represents email
    private String password;
}
