package com.github.pawelkow.exception.resolver;

/**
 * Exception resolver capable of providing object reference as result.
 *
 * @author pawelkowalski92
 * @see com.github.pawelkow.exception.resolver.ExceptionResolver
 */
public interface ReferenceResolver<R> extends ExceptionResolver {

    /**
     * Resolve provided exception and provide an object reference.
     *
     * @param exception exception to be resolved
     * @return object reference
     */
    R resolve(Throwable exception);

}