package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.LongToDoubleFunction;

/**
 * Extension for standard {@link LongToDoubleFunction} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongToDoubleFunction<X extends Exception> extends LongToDoubleFunction, Checked.WithDouble<LongToDoubleFunction, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public double transform(long value) throws X {
     *      ...
     *  }
     *  longStream.mapToDouble(wrap(this::transform).returnFallback(Double.NaN));
     * </pre>
     *
     * @param longToDoubleFunction {@link CheckedLongToDoubleFunction} to be wrapped
     * @param <X>                  {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongToDoubleFunction<X> wrap(CheckedLongToDoubleFunction<? extends X> longToDoubleFunction) {
        return (CheckedLongToDoubleFunction<X>) longToDoubleFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    double applyAsDoubleWithException(long value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default double applyAsDouble(long value) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(value);
    }

    @Override
    default LongToDoubleFunction handleException(DoubleResolver handler) {
        return value -> {
            try {
                return applyAsDoubleWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
