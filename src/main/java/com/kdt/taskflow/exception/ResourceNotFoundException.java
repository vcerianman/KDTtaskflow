package com.kdt.taskflow.exception;

/**Ném ra khi thao tác trên một project không tồn tại.*/
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
