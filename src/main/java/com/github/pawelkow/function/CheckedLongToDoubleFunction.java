package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface CheckedLongToDoubleFunction<X extends Exception> extends LongToDoubleFunction, Checked.WithDouble<LongToDoubleFunction, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongToDoubleFunction<X> wrap(CheckedLongToDoubleFunction<? extends X> function) {
        return (CheckedLongToDoubleFunction<X>) function;
    }

    double applyAsDoubleWithException(long value) throws X;

    @Override
    default double applyAsDouble(long value) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(value);
    }

    @Override
    default LongToDoubleFunction handleException(DoubleResolver handler) {
        return value -> {
            try {
                return applyAsDoubleWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
