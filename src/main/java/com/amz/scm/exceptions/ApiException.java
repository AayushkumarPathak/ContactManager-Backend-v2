package com.amz.scm.exceptions;

public class ApiException extends RuntimeException{
    public ApiException(){
        super();
    }
    public ApiException(String msg){
        super(msg);
    }
    
}
