package com.github.pawelkow.exception.resolver;

public interface BooleanResolver extends ExceptionResolver {

    boolean resolve(Throwable exception);

}
