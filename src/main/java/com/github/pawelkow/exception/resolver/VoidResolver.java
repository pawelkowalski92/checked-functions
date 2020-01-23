package com.github.pawelkow.exception.resolver;

public interface VoidResolver extends ExceptionResolver {

    void resolve(Throwable exception);

}
