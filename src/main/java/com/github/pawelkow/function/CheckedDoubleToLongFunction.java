package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.DoubleToLongFunction;

@FunctionalInterface
public interface CheckedDoubleToLongFunction<X extends Exception> extends DoubleToLongFunction, Checked.WithLong<DoubleToLongFunction, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleToLongFunction<X> wrap(CheckedDoubleToLongFunction<? extends X> doubleToLongFunction) {
        return (CheckedDoubleToLongFunction<X>) doubleToLongFunction;
    }

    long applyAsLongWithException(double value) throws X;

    @Override
    default long applyAsLong(double value) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(value);
    }

    @Override
    default DoubleToLongFunction handleException(LongResolver handler) {
        return value -> {
            try {
                return applyAsLongWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
