package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.IntToDoubleFunction;

/**
 * Extension for standard {@link IntToDoubleFunction} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntToDoubleFunction<X extends Exception> extends IntToDoubleFunction, Checked.WithDouble<IntToDoubleFunction, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public double transform(int value) throws X {
     *      ...
     *  }
     *  intStream.mapToDouble(wrap(this::transform).returnFallback(Double.NaN));
     * </pre>
     *
     * @param intToDoubleFunction {@link CheckedIntToDoubleFunction} to be wrapped
     * @param <X>                 {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntToDoubleFunction<X> wrap(CheckedIntToDoubleFunction<? extends X> intToDoubleFunction) {
        return (CheckedIntToDoubleFunction<X>) intToDoubleFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    double applyAsDoubleWithException(int value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default double applyAsDouble(int value) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(value);
    }

    @Override
    default IntToDoubleFunction handleException(DoubleResolver handler) {
        return value -> {
            try {
                return applyAsDoubleWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
