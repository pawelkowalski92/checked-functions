package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.Supplier;

@FunctionalInterface
public interface CheckedSupplier<T, X extends Exception> extends Supplier<T>, Checked.WithValue<T, Supplier<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedSupplier<T, X> wrap(CheckedSupplier<? extends T, ? extends X> supplier) {
        return (CheckedSupplier<T, X>) supplier;
    }

    T getWithException() throws X;

    @Override
    default T get() {
        return handleException(RETHROW_UNCHECKED).get();
    }

    @Override
    default Supplier<T> handleException(ReferenceResolver<T> handler) {
        return () -> {
            try {
                return getWithException();
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
