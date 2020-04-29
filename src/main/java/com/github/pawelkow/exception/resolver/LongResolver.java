package com.github.pawelkow.exception.resolver;

/**
 * Exception resolver capable of providing long primitive as result.
 *
 * @author pawelkowalski92
 * @see com.github.pawelkow.exception.resolver.ExceptionResolver
 */
public interface LongResolver extends ExceptionResolver {

    /**
     * Resolve provided exception and provide a long primitive.
     *
     * @param exception exception to be resolved
     * @return long primitive
     */
    long resolve(Throwable exception);

}
