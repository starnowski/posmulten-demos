package com.github.starnowski.posmulten.demos.hibernate.exceptions;

public class InvalidTenantContext extends RuntimeException{

    public InvalidTenantContext() {
    }

    public InvalidTenantContext(String message) {
        super(message);
    }

    public InvalidTenantContext(String message, Throwable cause) {
        super(message, cause);
    }
}
