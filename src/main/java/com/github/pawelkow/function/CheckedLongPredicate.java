package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.LongPredicate;

/**
 * Extension for standard {@link LongPredicate} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongPredicate<X extends Exception> extends LongPredicate, Checked.WithBoolean<LongPredicate, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public boolean isApplicable(long value) throws X {
     *      ...
     *  }
     *  longStream.allMatch(wrap(this::filter).returnFallback(false));
     * </pre>
     *
     * @param longPredicate {@link CheckedLongPredicate} to be wrapped
     * @param <X>           {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongPredicate<X> wrap(CheckedLongPredicate<? extends X> longPredicate) {
        return (CheckedLongPredicate<X>) longPredicate;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param value the input argument
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     * @throws X {@link Exception exception(s)} related to this predicate
     */
    boolean testWithException(long value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default boolean test(long value) {
        return handleException(RETHROW_UNCHECKED).test(value);
    }

    @Override
    default LongPredicate handleException(BooleanResolver handler) {
        return value -> {
            try {
                return testWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
