package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.IntFunction;

/**
 * Extension for standard {@link IntFunction} that supports checked exceptions.
 *
 * @param <R> the type of the result of the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntFunction<R, X extends Exception> extends IntFunction<R>, Checked.WithValue<R, IntFunction<R>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public R transform(int value) throws X {
     *      ...
     *  }
     *  public R fallback() {
     *      ...
     *  }
     *  intStream.mapToObj(wrap(this::transform).supplyFallback(this::fallback));
     * </pre>
     *
     * @param intFunction {@link CheckedIntFunction} to be wrapped
     * @param <R>         the type of the result of the function
     * @param <X>         {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <R, X extends Exception> CheckedIntFunction<R, X> wrap(CheckedIntFunction<? extends R, ? extends X> intFunction) {
        return (CheckedIntFunction<R, X>) intFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    R applyWithException(int value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default R apply(int value) {
        return handleException(RETHROW_UNCHECKED).apply(value);
    }

    @Override
    default IntFunction<R> handleException(ReferenceResolver<? extends R> handler) {
        return value -> {
            try {
                return applyWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
