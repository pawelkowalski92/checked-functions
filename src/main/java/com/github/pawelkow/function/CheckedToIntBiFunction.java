package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.ToIntBiFunction;

/**
 * Extension for standard {@link ToIntBiFunction} that supports checked exceptions.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedToIntBiFunction<T, U, X extends Exception> extends ToIntBiFunction<T, U>, Checked.WithInt<ToIntBiFunction<T, U>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public int reduce(T t, U u) throws X {
     *      ...
     *  }
     *
     *  concurrentHashMap.reduceToInt(100l, wrap(this::reduce).returnFallback(0), Integer::sum);
     * </pre>
     *
     * @param toIntBiFunction {@link CheckedToIntBiFunction} to be wrapped
     * @param <T>             the type of the first argument to the function
     * @param <U>             the type of the second argument to the function
     * @param <X>             {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedToIntBiFunction<T, U, X> wrap(CheckedToIntBiFunction<? super T, ? super U, ? extends X> toIntBiFunction) {
        return (CheckedToIntBiFunction<T, U, X>) toIntBiFunction;
    }

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    int applyAsIntWithException(T t, U u) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default int applyAsInt(T t, U u) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(t, u);
    }

    @Override
    default ToIntBiFunction<T, U> handleException(IntResolver handler) {
        return (t, u) -> {
            try {
                return applyAsIntWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
