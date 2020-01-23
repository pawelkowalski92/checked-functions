package com.github.pawelkow.exception.resolver;

public interface IntResolver extends ExceptionResolver {

    int resolve(Throwable exception);

}
