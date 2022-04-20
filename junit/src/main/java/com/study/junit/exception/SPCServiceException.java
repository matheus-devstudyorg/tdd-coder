package com.study.junit.exception;

public class SPCServiceException extends RuntimeException{
    public SPCServiceException(Exception e){
        super(e);
    }
}
