package org.example.exception;

import lombok.Getter;

public class CustomerException extends RuntimeException {

    @Getter
    private String code;
    private String message;

    public CustomerException(String code,String message) {
        this.code = code;
        this.message = message;
    }

    public CustomerException(String message) {
        this.code = "500";
        this.message = message;
    }

    public CustomerException() {}

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
