package com.github.pawelkow.exception.resolver;

public interface LongResolver extends ExceptionResolver {

    long resolve(Throwable exception);

}
