package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.DoubleSupplier;

/**
 * Extension for standard {@link DoubleSupplier} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoubleSupplier<X extends Exception> extends DoubleSupplier, Checked.WithDouble<DoubleSupplier, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public double fallback() throws X {
     *      ...
     *  }
     *  optionalDouble.orElseGet(wrap(this::fallback).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param doubleSupplier {@link CheckedDoubleSupplier} to be wrapped
     * @param <X>            {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleSupplier<X> wrap(CheckedDoubleSupplier<? extends X> doubleSupplier) {
        return (CheckedDoubleSupplier<X>) doubleSupplier;
    }

    /**
     * Gets a result.
     *
     * @return a result
     * @throws X {@link Exception exception(s)} related to this supplier
     */
    double getAsDoubleWithException() throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default double getAsDouble() {
        return handleException(RETHROW_UNCHECKED).getAsDouble();
    }

    @Override
    default DoubleSupplier handleException(DoubleResolver handler) {
        return () -> {
            try {
                return getAsDoubleWithException();
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
