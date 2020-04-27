package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.DoubleToIntFunction;

/**
 * Extension for standard {@link DoubleToIntFunction} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoubleToIntFunction<X extends Exception> extends DoubleToIntFunction, Checked.WithInt<DoubleToIntFunction, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public int transform(double value) throws X {
     *      ...
     *  }
     *  doubleStream.mapToInt(wrap(this::transform).returnFallback(-1));
     * </pre>
     *
     * @param doubleToIntFunction {@link CheckedDoubleToIntFunction} to be wrapped
     * @param <X>                 {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleToIntFunction<X> wrap(CheckedDoubleToIntFunction<? extends X> doubleToIntFunction) {
        return (CheckedDoubleToIntFunction<X>) doubleToIntFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    int applyAsIntWithException(double value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default int applyAsInt(double value) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(value);
    }

    @Override
    default DoubleToIntFunction handleException(IntResolver handler) {
        return value -> {
            try {
                return applyAsIntWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
