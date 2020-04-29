package com.github.pawelkow.exception.resolver;

import java.util.function.Predicate;

/**
 * Common base for family of interfaces responsible for resolution of exception into desired result.
 *
 * @author pawelkowalski92
 */
public interface ExceptionResolver {


    /**
     * Utility method for filtering collections/streams of different resolvers, e.g.
     *
     * <pre>
     *  resolversStream.filter(supports(ex)).findFirst();
     * </pre>
     *
     * @param exception exception to be resolved
     * @return {@link Predicate} testing resolvers eligibility
     */
    static Predicate<ExceptionResolver> supports(Throwable exception) {
        return resolver -> resolver.isSupported(exception);
    }

    /**
     * Determine if provided exception is eligible for resolution.
     *
     * @param exception exception to be resolved
     * @return {@code true} if exception is supported by this resolver, otherwise {@code false}
     */
    boolean isSupported(Throwable exception);

}
