package com.github.pawelkow.function;

/**
 * Default exception used to encapsulate checked exceptions thrown by one of {@link Checked} functional interfaces.
 */
public class UnhandledCheckedException extends RuntimeException {

    /**
     * Used to construct new exception wrapper.
     *
     * @param cause exception to be encapsulated
     */
    public UnhandledCheckedException(Exception cause) {
        super(cause);
    }

}
