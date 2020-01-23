package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface CheckedDoubleToIntFunction<X extends Exception> extends DoubleToIntFunction, Checked.WithInt<DoubleToIntFunction, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleToIntFunction<X> wrap(CheckedDoubleToIntFunction<? extends X> doubleToIntFunction) {
        return (CheckedDoubleToIntFunction<X>) doubleToIntFunction;
    }

    int applyAsIntWithException(double value) throws X;

    @Override
    default int applyAsInt(double value) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(value);
    }

    @Override
    default DoubleToIntFunction handleException(IntResolver handler) {
        return value -> {
            try {
                return applyAsIntWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
