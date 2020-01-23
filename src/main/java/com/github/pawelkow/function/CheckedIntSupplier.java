package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.IntSupplier;

@FunctionalInterface
public interface CheckedIntSupplier<X extends Exception> extends IntSupplier, Checked.WithInt<IntSupplier, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntSupplier<X> wrap(CheckedIntSupplier<? extends X> intSupplier) {
        return (CheckedIntSupplier<X>) intSupplier;
    }

    int getAsIntWithException() throws X;

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
