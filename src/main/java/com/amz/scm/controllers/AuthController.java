package com.amz.scm.controllers;

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

import com.amz.scm.exceptions.ApiException;
import com.amz.scm.models.User;
import com.amz.scm.payloads.JwtAuthRequest;
import com.amz.scm.payloads.JwtAuthResponse;
import com.amz.scm.payloads.UserDto;
import com.amz.scm.security.JwtTokenHelper;

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


    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest request){

        try {
            // We're using email as the username
            this.authenticate(request.getUsername(), request.getPassword());

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());

            String token = this.jwtTokenHelper.generateToken(userDetails);

            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(token);
            response.setUserDto(this.modelMapper.map((User)userDetails, UserDto.class));

            return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.OK);
        } catch (Exception e) {
           throw new ApiException("Invalid email or password Please try again!!");
        }
      
    }

    private void authenticate(String email, String password) throws Exception{
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        try{
            this.authenticationManager.authenticate(authenticationToken);
        }
        catch(BadCredentialsException e){
            System.out.println("Invalid Credentials");
           throw new ApiException("Invalid email or password. Try Again");
        }
    }

}
