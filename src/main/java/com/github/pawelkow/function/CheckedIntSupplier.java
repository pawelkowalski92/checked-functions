package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.IntSupplier;

/**
 * Extension for standard {@link IntSupplier} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntSupplier<X extends Exception> extends IntSupplier, Checked.WithInt<IntSupplier, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public int fallback() throws X {
     *      ...
     *  }
     *  optionalInt.orElseGet(wrap(this::fallback).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param intSupplier {@link CheckedIntSupplier} to be wrapped
     * @param <X>         {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntSupplier<X> wrap(CheckedIntSupplier<? extends X> intSupplier) {
        return (CheckedIntSupplier<X>) intSupplier;
    }

    /**
     * Gets a result.
     *
     * @return a result
     * @throws X {@link Exception exception(s)} related to this supplier
     */
    int getAsIntWithException() throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default int getAsInt() {
        return handleException(RETHROW_UNCHECKED).getAsInt();
    }

    @Override
    default IntSupplier handleException(IntResolver handler) {
        return () -> {
            try {
                return getAsIntWithException();
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
