package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.DoubleSupplier;

@FunctionalInterface
public interface CheckedDoubleSupplier<X extends Exception> extends DoubleSupplier, Checked.WithDouble<DoubleSupplier, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleSupplier<X> wrap(CheckedDoubleSupplier<? extends X> doubleSupplier) {
        return (CheckedDoubleSupplier<X>) doubleSupplier;
    }

    double getAsDoubleWithException() throws X;

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
