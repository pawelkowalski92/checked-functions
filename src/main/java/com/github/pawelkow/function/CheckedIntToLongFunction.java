package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.DoubleToLongFunction;
import java.util.function.IntToLongFunction;

/**
 * Extension for standard {@link IntToLongFunction} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntToLongFunction<X extends Exception> extends IntToLongFunction, Checked.WithLong<IntToLongFunction, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public long transform(int value) throws X {
     *      ...
     *  }
     *  intStream.mapToLong(wrap(this::transform).returnFallback(-1l));
     * </pre>
     *
     * @param intToLongFunction {@link CheckedIntToLongFunction} to be wrapped
     * @param <X>               {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntToLongFunction<X> wrap(CheckedIntToLongFunction<? extends X> intToLongFunction) {
        return (CheckedIntToLongFunction<X>) intToLongFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    long applyAsLongWithException(int value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default long applyAsLong(int value) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(value);
    }

    @Override
    default IntToLongFunction handleException(LongResolver handler) {
        return value -> {
            try {
                return applyAsLongWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
