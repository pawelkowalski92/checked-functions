package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.ToLongFunction;

/**
 * Extension for standard {@link ToLongFunction} that supports checked exceptions.
 *
 * @param <T> the type of the input to the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedToLongFunction<T, X extends Exception> extends ToLongFunction<T>, Checked.WithLong<ToLongFunction<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public long transform(T t) throws X {
     *      ...
     *  }
     *  stream.mapToLong(wrap(this::transform).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param toLongFunction {@link CheckedToLongFunction} to be wrapped
     * @param <T>            the type of the input to the function
     * @param <X>            {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedToLongFunction<T, X> wrap(CheckedToLongFunction<? super T, ? extends X> toLongFunction) {
        return (CheckedToLongFunction<T, X>) toLongFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    long applyAsLongWithException(T t) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default long applyAsLong(T t) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(t);
    }

    @Override
    default ToLongFunction<T> handleException(LongResolver handler) {
        return t -> {
            try {
                return applyAsLongWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
