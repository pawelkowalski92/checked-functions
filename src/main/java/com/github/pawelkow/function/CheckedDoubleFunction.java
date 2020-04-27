package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.DoubleFunction;

/**
 * Extension for standard {@link DoubleFunction} that supports checked exceptions.
 *
 * @param <R> the type of the result of the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoubleFunction<R, X extends Exception> extends DoubleFunction<R>, Checked.WithValue<R, DoubleFunction<R>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public R transform(double value) throws X {
     *      ...
     *  }
     *  public R fallback() {
     *      ...
     *  }
     *  doubleStream.mapToObj(wrap(this::transform).supplyFallback(this::fallback));
     * </pre>
     *
     * @param doubleFunction {@link CheckedDoubleFunction} to be wrapped
     * @param <R>            the type of the result of the function
     * @param <X>            {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <R, X extends Exception> CheckedDoubleFunction<R, X> wrap(CheckedDoubleFunction<? extends R, ? extends X> doubleFunction) {
        return (CheckedDoubleFunction<R, X>) doubleFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    R applyWithException(double value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default R apply(double value) {
        return handleException(RETHROW_UNCHECKED).apply(value);
    }

    @Override
    default DoubleFunction<R> handleException(ReferenceResolver<? extends R> handler) {
        return value -> {
            try {
                return applyWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
