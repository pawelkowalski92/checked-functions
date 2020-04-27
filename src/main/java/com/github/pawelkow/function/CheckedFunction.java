package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.Function;

/**
 * Extension for standard {@link Function} that supports checked exceptions.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedFunction<T, R, X extends Exception> extends Function<T, R>, Checked.WithValue<R, Function<T, R>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public R transform(T t) throws X {
     *      ...
     *  }
     *  stream.map(wrap(this::transform).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param function {@link CheckedFunction} to be wrapped
     * @param <T>      the type of the input to the function
     * @param <R>      the type of the result of the function
     * @param <X>      {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, R, X extends Exception> CheckedFunction<T, R, X> wrap(CheckedFunction<? super T, ? extends R, ? extends X> function) {
        return (CheckedFunction<T, R, X>) function;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    R applyWithException(T t) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default R apply(T t) {
        return handleException(RETHROW_UNCHECKED).apply(t);
    }

    @Override
    default Function<T, R> handleException(ReferenceResolver<? extends R> handler) {
        return t -> {
            try {
                return applyWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
