package com.amz.scm.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amz.scm.apiResponses.ApiResponseEntity;
import com.amz.scm.payloads.UserDto;
import com.amz.scm.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public ResponseEntity<?> testApi() {
        return new ResponseEntity<>(new ApiResponseEntity<>(null, true, "API is working", null, 200), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto savedUser = this.userService.saveUser(userDto);

            return new ResponseEntity<>(
                    new ApiResponseEntity<>(savedUser, true, "User created", null, 200),
                    HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(
                            null, false, "User not created", e.getMessage(), 500),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> users = this.userService.getAllUsers();
        return new ResponseEntity<>(new ApiResponseEntity<>(users, true, "Users fetched", null, 200), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id) {
        try {
            UserDto user = this.userService.getUserById(id);
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(user, true, "User fetched", null, 200),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(null, false, "User not found", e.getMessage(), 404),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        try {
            UserDto updatedUser = this.userService.updateUser(userDto, id);
            return new ResponseEntity<>(new ApiResponseEntity<>(
                    updatedUser,
                    true,
                    "User updated",
                    null,
                    200),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(null, false, "User not updated", e.getMessage(), 500),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ONLY ADMIN CAN DELETE USER -> {}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try {
            this.userService.deleteUser(id);
            return new ResponseEntity<>(new ApiResponseEntity<>(null, true, "User deleted", null, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponseEntity<>(null, false, "User not deleted", e.getMessage(), 500),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
