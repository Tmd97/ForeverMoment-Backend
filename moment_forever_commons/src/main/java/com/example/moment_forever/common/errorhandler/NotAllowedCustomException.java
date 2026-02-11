package com.example.moment_forever.common.errorhandler;

public class NotAllowedCustomException extends RuntimeException{
    public NotAllowedCustomException(String message) {
        super(message);
    }

}
