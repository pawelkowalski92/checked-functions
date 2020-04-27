package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.IntPredicate;

/**
 * Extension for standard {@link IntPredicate} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntPredicate<X extends Exception> extends IntPredicate, Checked.WithBoolean<IntPredicate, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public boolean isApplicable(int value) throws X {
     *      ...
     *  }
     *  intStream.allMatch(wrap(this::filter).returnFallback(false));
     * </pre>
     *
     * @param intPredicate {@link CheckedIntPredicate} to be wrapped
     * @param <X>          {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntPredicate<X> wrap(CheckedIntPredicate<? extends X> intPredicate) {
        return (CheckedIntPredicate<X>) intPredicate;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param value the input argument
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     * @throws X {@link Exception exception(s)} related to this predicate
     */
    boolean testWithException(int value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default boolean test(int value) {
        return handleException(RETHROW_UNCHECKED).test(value);
    }

    @Override
    default IntPredicate handleException(BooleanResolver handler) {
        return value -> {
            try {
                return testWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
