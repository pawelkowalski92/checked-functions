package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.ExceptionResolver;
import com.github.pawelkow.function.Checked;

/**
 * Thrown when {@link Checked} interface is provided with {@link ExceptionHandler} that doesn't configure proper {@link ExceptionResolver}.
 */
public class ExceptionHandlerMisconfigurationException extends IllegalStateException {

    private final Class<? extends Throwable> missedExceptionType;

    /**
     * Used to construct exception without specifying any cause.
     *
     * @param missedExceptionType expected {@link Throwable} type that should've been caught
     */
    public ExceptionHandlerMisconfigurationException(Class<? extends Throwable> missedExceptionType) {
        super();
        this.missedExceptionType = missedExceptionType;
    }

    /**
     * Used to construct exception including information what caused it.
     *
     * @param exception {@link Throwable} that should've been caught
     */
    public ExceptionHandlerMisconfigurationException(Throwable exception) {
        super(exception);
        this.missedExceptionType = exception.getClass();
    }

    /**
     * Gets {@link Throwable} type that caused this exception.
     *
     * @return missed type
     */
    public Class<? extends Throwable> getMissedExceptionType() {
        return missedExceptionType;
    }

}
