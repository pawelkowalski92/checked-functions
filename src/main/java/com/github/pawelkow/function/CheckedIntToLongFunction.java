package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface CheckedIntToLongFunction<X extends Exception> extends IntToLongFunction, Checked.WithLong<IntToLongFunction, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntToLongFunction<X> wrap(CheckedIntToLongFunction<? extends X> doubleToLongFunction) {
        return (CheckedIntToLongFunction<X>) doubleToLongFunction;
    }

    long applyAsLongWithException(int value) throws X;

    @Override
    default long applyAsLong(int value) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(value);
    }

    @Override
    default IntToLongFunction handleException(LongResolver handler) {
        return value -> {
            try {
                return applyAsLongWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
