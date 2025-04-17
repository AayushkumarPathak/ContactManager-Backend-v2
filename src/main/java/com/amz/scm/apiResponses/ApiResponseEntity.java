package com.amz.scm.apiResponses;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ApiResponseEntity<T> implements ApiResponse<T>{

    private boolean success;
    private String message;
    private T data;
    private Object errors;
    private int statusCode;

    public ApiResponseEntity(){
        this.success = false;
        this.message = "Something went wrong";
        this.data = null;
        this.errors = null;
        this.statusCode = 500;
    }

    public ApiResponseEntity(T obj, boolean success,String msg,Object errors,int statusCode){
        this.success = success;
        this.message = msg;
        this.data = obj;
        this.errors = errors;
        this.statusCode = statusCode;
    }


}
