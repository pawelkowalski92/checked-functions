package com.github.pawelkow.exception.resolver;

/**
 * Exception resolver capable of providing double primitive as result.
 *
 * @author pawelkowalski92
 * @see com.github.pawelkow.exception.resolver.ExceptionResolver
 */
public interface DoubleResolver extends ExceptionResolver {

    /**
     * Resolve provided exception and provide a double primitive.
     *
     * @param exception exception to be resolved
     * @return double primitive
     */
    double resolve(Throwable exception);

}
