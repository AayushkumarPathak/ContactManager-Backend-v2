package com.amz.scm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.amz.scm.apiResponses.ApiResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseEntity<?>> resourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponseEntity<?> response = new ApiResponseEntity<>(null, false, ex.getMessage(), null, 404);
        return new ResponseEntity<>(response, null, 404);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseEntity<?>> argumentNotValidException(MethodArgumentNotValidException ex) {
        ApiResponseEntity<?> response = new ApiResponseEntity<>();

        StringBuilder message = new StringBuilder("Validation failed: ");

        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String field = ((FieldError) err).getField();
            String defaultMsg = err.getDefaultMessage();
            message.append(String.format("[%s: %s] ", field, defaultMsg));
        });

        response.setMessage(message.toString().trim());
        response.setStatusCode(400); 

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseEntity<?>> apiException(ApiException ex){
        String msg = ex.getMessage();
        ApiResponseEntity<?> responseEntity = new ApiResponseEntity<>();

        responseEntity.setMessage(msg);
        responseEntity.setStatusCode(400);
        responseEntity.setData(null);
        responseEntity.setErrors(ex.getMessage());

        return new ResponseEntity<>(responseEntity,HttpStatus.BAD_REQUEST);
    }

}
