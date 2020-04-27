package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.DoublePredicate;

/**
 * Extension for standard {@link DoublePredicate} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoublePredicate<X extends Exception> extends DoublePredicate, Checked.WithBoolean<DoublePredicate, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public boolean isApplicable(double value) throws X {
     *      ...
     *  }
     *  doubleStream.allMatch(wrap(this::filter).returnFallback(false));
     * </pre>
     *
     * @param doublePredicate {@link CheckedDoublePredicate} to be wrapped
     * @param <X>             {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoublePredicate<X> wrap(CheckedDoublePredicate<? extends X> doublePredicate) {
        return (CheckedDoublePredicate<X>) doublePredicate;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param value the input argument
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     * @throws X {@link Exception exception(s)} related to this predicate
     */
    boolean testWithException(double value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default boolean test(double value) {
        return handleException(RETHROW_UNCHECKED).test(value);
    }

    @Override
    default DoublePredicate handleException(BooleanResolver handler) {
        return value -> {
            try {
                return testWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
