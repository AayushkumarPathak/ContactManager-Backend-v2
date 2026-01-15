package com.techmagnet.scm.exceptions;

public class ImageUploadException extends RuntimeException {

    private String message;

    public ImageUploadException(String msg){
        super(msg);
    }
    


}
