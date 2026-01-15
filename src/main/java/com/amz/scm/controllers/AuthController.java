package com.techmagnet.scm.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techmagnet.scm.apiResponses.ApiResponseEntity;
import com.techmagnet.scm.exceptions.ApiException;
import com.techmagnet.scm.models.User;
import com.techmagnet.scm.payloads.JwtAuthRequest;
import com.techmagnet.scm.payloads.JwtAuthResponse;
import com.techmagnet.scm.payloads.UserDto;
import com.techmagnet.scm.security.JwtTokenHelper;
import com.techmagnet.scm.services.UserService;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseEntity<?>> registerUser(@RequestBody UserDto userDto) {
        boolean isUserExists = this.userService.checkUserAlreadyExists(userDto.getUsername(), userDto.getEmail());

        if (isUserExists) {
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(null, false, "User with given email or username already exists", null, 409),
                    HttpStatus.CONFLICT
            );
        }

        UserDto registeredUser = this.userService.registerUser(userDto);

        ApiResponseEntity<UserDto> res = new ApiResponseEntity<>();

        res.setData(registeredUser);
        res.setErrors(null);
        res.setMessage("User registered successfully");
        res.setSuccess(true);
        res.setStatusCode(HttpStatus.CREATED.value());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest request) {

        try {
            // We're using email as the username
            this.authenticate(request.getUsername(), request.getPassword());

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());

            String token = this.jwtTokenHelper.generateToken(userDetails);

            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(token);
            response.setUserDto(this.modelMapper.map((User) userDetails, UserDto.class));

            return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiException("Invalid email or password Please try again!!");
        }

    }

    private void authenticate(String email, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password);

        try {
            this.authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            System.out.println("Invalid Credentials");
            throw new ApiException("Invalid email or password. Try Again");
        }
    }

}
