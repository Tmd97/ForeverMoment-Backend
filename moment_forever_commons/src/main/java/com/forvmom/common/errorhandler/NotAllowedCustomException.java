package com.forvmom.common.errorhandler;

public class NotAllowedCustomException extends RuntimeException{
    public NotAllowedCustomException(String message) {
        super(message);
    }

}
