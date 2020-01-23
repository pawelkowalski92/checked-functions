package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface CheckedIntToDoubleFunction<X extends Exception> extends IntToDoubleFunction, Checked.WithDouble<IntToDoubleFunction, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntToDoubleFunction<X> wrap(CheckedIntToDoubleFunction<? extends X> doubleToDoubleFunction) {
        return (CheckedIntToDoubleFunction<X>) doubleToDoubleFunction;
    }

    double applyAsDoubleWithException(int value) throws X;

    @Override
    default double applyAsDouble(int value) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(value);
    }

    @Override
    default IntToDoubleFunction handleException(DoubleResolver handler) {
        return value -> {
            try {
                return applyAsDoubleWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
