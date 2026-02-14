package com.forvmom.common.errorhandler;

public class CustomAuthException extends RuntimeException{
    public CustomAuthException(String msg) {
        super(msg);
    }

}
