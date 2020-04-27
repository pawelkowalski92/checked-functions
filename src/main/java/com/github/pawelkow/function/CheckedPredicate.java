package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.Predicate;

/**
 * Extension for standard {@link Predicate} that supports checked exceptions.
 *
 * @param <T> the type of the input to the predicate
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedPredicate<T, X extends Exception> extends Predicate<T>, Checked.WithBoolean<Predicate<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public boolean isApplicable(T t) throws X {
     *      ...
     *  }
     *  optional.filter(wrap(this::isApplicable).returnFallback(false))
     *          .orElse(null);
     * </pre>
     *
     * @param predicate {@link CheckedPredicate} to be wrapped
     * @param <T>       the type of the input to the predicate
     * @param <X>       {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedPredicate<T, X> wrap(CheckedPredicate<? super T, ? extends X> predicate) {
        return (CheckedPredicate<T, X>) predicate;
    }

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     * @throws X {@link Exception exception(s)} related to this predicate
     */
    boolean testWithException(T t) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default boolean test(T t) {
        return handleException(RETHROW_UNCHECKED).test(t);
    }

    @Override
    default Predicate<T> handleException(BooleanResolver handler) {
        return t -> {
            try {
                return testWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
