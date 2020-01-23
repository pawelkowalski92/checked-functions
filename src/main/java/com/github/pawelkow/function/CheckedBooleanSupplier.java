package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface CheckedBooleanSupplier<X extends Exception> extends BooleanSupplier, Checked.WithBoolean<BooleanSupplier, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedBooleanSupplier<X> wrap(CheckedBooleanSupplier<? extends X> booleanSupplier) {
        return (CheckedBooleanSupplier<X>) booleanSupplier;
    }

    boolean getAsBooleanWithException() throws X;

    @Override
    default boolean getAsBoolean() {
        return handleException(RETHROW_UNCHECKED).getAsBoolean();
    }

    @Override
    default BooleanSupplier handleException(BooleanResolver handler) {
        return () -> {
            try {
                return getAsBooleanWithException();
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
