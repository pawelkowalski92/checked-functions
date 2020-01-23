package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.LongSupplier;

@FunctionalInterface
public interface CheckedLongSupplier<X extends Exception> extends LongSupplier, Checked.WithLong<LongSupplier, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongSupplier<X> wrap(CheckedLongSupplier<? extends X> supplier) {
        return (CheckedLongSupplier<X>) supplier;
    }

    long getAsLongWithException() throws X;

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
