package com.github.pawelkow.exception.resolver;

/**
 * Exception resolver capable of providing boolean primitive as result.
 *
 * @author pawelkowalski92
 * @see com.github.pawelkow.exception.resolver.ExceptionResolver
 */
public interface BooleanResolver extends ExceptionResolver {

    /**
     * Resolve provided exception and provide a boolean primitive.
     *
     * @param exception exception to be resolved
     * @return boolean primitive
     */
    boolean resolve(Throwable exception);

}
