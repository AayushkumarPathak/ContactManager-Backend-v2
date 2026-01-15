package com.techmagnet.scm.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private String field;

    public ResourceNotFoundException(String resourceName, String fieldName, String field) {
        super(String.format("%s not found with %s : %s",resourceName,fieldName,field));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

}
