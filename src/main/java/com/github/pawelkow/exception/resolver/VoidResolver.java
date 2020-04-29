package com.github.pawelkow.exception.resolver;

/**
 * Exception resolver capable of consuming the exception without any result.
 *
 * @author pawelkowalski92
 * @see com.github.pawelkow.exception.resolver.ExceptionResolver
 */
public interface VoidResolver extends ExceptionResolver {

    /**
     * Resolve provided exception without providing any result.
     *
     * @param exception exception to be resolved
     */
    void resolve(Throwable exception);

}
