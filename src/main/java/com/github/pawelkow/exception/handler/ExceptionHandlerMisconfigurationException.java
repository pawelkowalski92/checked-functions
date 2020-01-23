package com.github.pawelkow.exception.handler;

public class ExceptionHandlerMisconfigurationException extends IllegalStateException {

    private final Class<? extends Throwable> missedExceptionType;

    public ExceptionHandlerMisconfigurationException(Class<? extends Throwable> missedExceptionType) {
        super();
        this.missedExceptionType = missedExceptionType;
    }

    public ExceptionHandlerMisconfigurationException(Throwable exception) {
        super(exception);
        this.missedExceptionType = exception.getClass();
    }

    public Class<? extends Throwable> getMissedExceptionType() {
        return missedExceptionType;
    }

}
