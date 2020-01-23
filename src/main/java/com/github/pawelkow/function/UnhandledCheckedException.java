package com.github.pawelkow.function;

public class UnhandledCheckedException extends RuntimeException {

    public UnhandledCheckedException(Exception cause) {
        super(cause);
    }
    
}
