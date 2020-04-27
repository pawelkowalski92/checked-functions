package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.BiFunction;

/**
 * Extension for standard {@link BiFunction} that supports checked exceptions.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedBiFunction<T, U, R, X extends Exception> extends BiFunction<T, U, R>, Checked.WithValue<R, BiFunction<T, U, R>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public R transform(T t, U u) throws X {
     *      ...
     *  }
     *  public R fallback() {
     *      ...
     *  }
     *  map.replaceAll(wrap(this::transform).supplyFallback(this::fallback));
     * </pre>
     *
     * @param biFunction {@link CheckedBiFunction} to be wrapped
     * @param <T>        the type of the first argument to the function
     * @param <U>        the type of the second argument to the function
     * @param <R>        the type of the result of the function
     * @param <X>        {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, U, R, X extends Exception> CheckedBiFunction<T, U, R, X> wrap(CheckedBiFunction<? super T, ? super U, ? extends R, ? extends X> biFunction) {
        return (CheckedBiFunction<T, U, R, X>) biFunction;
    }

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    R applyWithException(T t, U u) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default R apply(T t, U u) {
        return handleException(RETHROW_UNCHECKED).apply(t, u);
    }

    @Override
    default BiFunction<T, U, R> handleException(ReferenceResolver<? extends R> handler) {
        return (t, u) -> {
            try {
                return applyWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
