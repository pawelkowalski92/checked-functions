package com.github.pawelkow.exception.resolver;

public interface DoubleResolver extends ExceptionResolver {

    double resolve(Throwable exception);

}
