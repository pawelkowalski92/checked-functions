package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.ToLongBiFunction;

/**
 * Extension for standard {@link ToLongBiFunction} that supports checked exceptions.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedToLongBiFunction<T, U, X extends Exception> extends ToLongBiFunction<T, U>, Checked.WithLong<ToLongBiFunction<T, U>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public long reduce(T t, U u) throws X {
     *      ...
     *  }
     *
     *  concurrentHashMap.reduceToLong(100l, wrap(this::reduce).returnFallback(0l), Long::sum);
     * </pre>
     *
     * @param toLongBiFunction {@link CheckedToLongBiFunction} to be wrapped
     * @param <T>              the type of the first argument to the function
     * @param <U>              the type of the second argument to the function
     * @param <X>              {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedToLongBiFunction<T, U, X> wrap(CheckedToLongBiFunction<? super T, ? super U, ? extends X> toLongBiFunction) {
        return (CheckedToLongBiFunction<T, U, X>) toLongBiFunction;
    }

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    long applyAsLongWithException(T t, U u) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default long applyAsLong(T t, U u) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(t, u);
    }

    @Override
    default ToLongBiFunction<T, U> handleException(LongResolver handler) {
        return (t, u) -> {
            try {
                return applyAsLongWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
