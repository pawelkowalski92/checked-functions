package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.ToDoubleFunction;

/**
 * Extension for standard {@link ToDoubleFunction} that supports checked exceptions.
 *
 * @param <T> the type of the input to the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedToDoubleFunction<T, X extends Exception> extends ToDoubleFunction<T>, Checked.WithDouble<ToDoubleFunction<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public double transform(T t) throws X {
     *      ...
     *  }
     *  stream.mapToDouble(wrap(this::transform).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param toDoubleFunction {@link CheckedToDoubleFunction} to be wrapped
     * @param <T>              the type of the input to the function
     * @param <X>              {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedToDoubleFunction<T, X> wrap(CheckedToDoubleFunction<? super T, ? extends X> toDoubleFunction) {
        return (CheckedToDoubleFunction<T, X>) toDoubleFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    double applyAsDoubleWithException(T t) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default double applyAsDouble(T t) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(t);
    }

    @Override
    default ToDoubleFunction<T> handleException(DoubleResolver handler) {
        return t -> {
            try {
                return applyAsDoubleWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
