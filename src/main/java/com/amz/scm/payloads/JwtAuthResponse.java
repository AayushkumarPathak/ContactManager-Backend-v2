package com.techmagnet.scm.payloads;

import lombok.Data;

@Data
public class JwtAuthResponse {
    private String token;
    private UserDto userDto;
}
