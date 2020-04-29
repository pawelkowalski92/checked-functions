package com.github.pawelkow.exception.resolver;

/**
 * Exception resolver capable of providing int primitive as result.
 *
 * @author pawelkowalski92
 * @see com.github.pawelkow.exception.resolver.ExceptionResolver
 */
public interface IntResolver extends ExceptionResolver {

    /**
     * Resolve provided exception and provide an int primitive.
     *
     * @param exception exception to be resolved
     * @return int primitive
     */
    int resolve(Throwable exception);

}
