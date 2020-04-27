package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.DoubleToLongFunction;

/**
 * Extension for standard {@link DoubleToLongFunction} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoubleToLongFunction<X extends Exception> extends DoubleToLongFunction, Checked.WithLong<DoubleToLongFunction, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public long transform(double value) throws X {
     *      ...
     *  }
     *  doubleStream.mapToLong(wrap(this::transform).returnFallback(-1l));
     * </pre>
     *
     * @param doubleToLongFunction {@link CheckedDoubleToLongFunction} to be wrapped
     * @param <X>                  {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleToLongFunction<X> wrap(CheckedDoubleToLongFunction<? extends X> doubleToLongFunction) {
        return (CheckedDoubleToLongFunction<X>) doubleToLongFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    long applyAsLongWithException(double value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default long applyAsLong(double value) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(value);
    }

    @Override
    default DoubleToLongFunction handleException(LongResolver handler) {
        return value -> {
            try {
                return applyAsLongWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
