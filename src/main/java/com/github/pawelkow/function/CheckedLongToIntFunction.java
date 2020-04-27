package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.LongToIntFunction;

/**
 * Extension for standard {@link LongToIntFunction} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongToIntFunction<X extends Exception> extends LongToIntFunction, Checked.WithInt<LongToIntFunction, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public int transform(long value) throws X {
     *      ...
     *  }
     *  longStream.mapToInt(wrap(this::transform).returnFallback(-1));
     * </pre>
     *
     * @param longToIntFunction {@link CheckedLongToIntFunction} to be wrapped
     * @param <X>               {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongToIntFunction<X> wrap(CheckedLongToIntFunction<? extends X> longToIntFunction) {
        return (CheckedLongToIntFunction<X>) longToIntFunction;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    int applyAsIntWithException(long value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default int applyAsInt(long value) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(value);
    }

    @Override
    default LongToIntFunction handleException(IntResolver handler) {
        return value -> {
            try {
                return applyAsIntWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
