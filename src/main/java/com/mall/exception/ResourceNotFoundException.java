package com.mall.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(404, resource + " not found with id: " + id);
    }

    public ResourceNotFoundException(String resource, String identifier) {
        super(404, resource + " not found: " + identifier);
    }
}