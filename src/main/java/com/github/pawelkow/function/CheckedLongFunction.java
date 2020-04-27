package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.LongFunction;

/**
 * Extension for standard {@link LongFunction} that supports checked exceptions.
 *
 * @param <R> the type of the result of the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongFunction<R, X extends Exception> extends LongFunction<R>, Checked.WithValue<R, LongFunction<R>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public R transform(long value) throws X {
     *      ...
     *  }
     *  public R fallback() {
     *      ...
     *  }
     *  longStream.mapToObj(wrap(this::transform).supplyValue(this::fallback));
     * </pre>
     *
     * @param longFunction {@link CheckedLongFunction} to be wrapped
     * @param <R>          the type of the result of the function
     * @param <X>          {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <R, X extends Exception> CheckedLongFunction<R, X> wrap(CheckedLongFunction<? extends R, ? extends X> longFunction) {
        return (CheckedLongFunction<R, X>) longFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    R applyWithException(long value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default R apply(long value) {
        return handleException(RETHROW_UNCHECKED).apply(value);
    }

    @Override
    default LongFunction<R> handleException(ReferenceResolver<? extends R> handler) {
        return value -> {
            try {
                return applyWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
