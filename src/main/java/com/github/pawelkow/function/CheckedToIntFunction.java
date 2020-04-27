package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.ToIntFunction;

/**
 * Extension for standard {@link ToIntFunction} that supports checked exceptions.
 *
 * @param <T> the type of the input to the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedToIntFunction<T, X extends Exception> extends ToIntFunction<T>, Checked.WithInt<ToIntFunction<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public int transform(T t) throws X {
     *      ...
     *  }
     *  stream.mapToInt(wrap(this::transform).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param toIntFunction {@link CheckedToIntFunction} to be wrapped
     * @param <T>           the type of the input to the function
     * @param <X>           {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedToIntFunction<T, X> wrap(CheckedToIntFunction<? super T, ? extends X> toIntFunction) {
        return (CheckedToIntFunction<T, X>) toIntFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    int applyAsIntWithException(T t) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default int applyAsInt(T t) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(t);
    }

    @Override
    default ToIntFunction<T> handleException(IntResolver handler) {
        return t -> {
            try {
                return applyAsIntWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
