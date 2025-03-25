package org.example.exception;

import lombok.Getter;

public class CustomerException extends RuntimeException {

    @Getter
    private String code;

    public CustomerException(String code,String message) {
        super(message);
        this.code = code;
    }

    public CustomerException(String message) {
        super(message);
        this.code = "500";
    }

    public CustomerException() {
        super();
        this.code = "500";
    }

}
