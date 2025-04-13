package com.amz.scm.apiResponses;

public interface ApiResponse<T> {
    T getData();

    String getMessage();

    boolean isSuccess();

    int getStatusCode();

    void setData(T data);

    void setMessage(String message);

    void setSuccess(boolean success);

    void setStatusCode(int statusCode);
}
