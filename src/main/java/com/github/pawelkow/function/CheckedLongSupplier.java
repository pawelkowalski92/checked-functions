package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.LongSupplier;

/**
 * Extension for standard {@link LongSupplier} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongSupplier<X extends Exception> extends LongSupplier, Checked.WithLong<LongSupplier, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public long fallback() throws X {
     *      ...
     *  }
     *  optionalLong.orElseGet(wrap(this::fallback).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param longSupplier {@link CheckedLongSupplier} to be wrapped
     * @param <X>          {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongSupplier<X> wrap(CheckedLongSupplier<? extends X> longSupplier) {
        return (CheckedLongSupplier<X>) longSupplier;
    }

    /**
     * Gets a result.
     *
     * @return a result
     * @throws X {@link Exception exception(s)} related to this supplier
     */
    long getAsLongWithException() throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default long getAsLong() {
        return handleException(RETHROW_UNCHECKED).getAsLong();
    }

    @Override
    default LongSupplier handleException(LongResolver handler) {
        return () -> {
            try {
                return getAsLongWithException();
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
