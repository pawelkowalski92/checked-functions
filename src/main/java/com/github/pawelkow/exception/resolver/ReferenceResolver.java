package com.github.pawelkow.exception.resolver;

public interface ReferenceResolver<R> extends ExceptionResolver {

    R resolve(Throwable exception);

}