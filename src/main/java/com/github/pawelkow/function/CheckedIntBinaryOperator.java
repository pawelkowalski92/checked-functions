package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface CheckedIntBinaryOperator<X extends Exception> extends IntBinaryOperator, Checked.WithInt<IntBinaryOperator, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntBinaryOperator<X> wrap(CheckedIntBinaryOperator<? extends X> binaryOperator) {
        return (CheckedIntBinaryOperator<X>) binaryOperator;
    }

    int applyAsIntWithException(int left, int right) throws X;

    @Override
    default int applyAsInt(int left, int right) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(left, right);
    }

    @Override
    default IntBinaryOperator handleException(IntResolver handler) {
        return (t, u) -> {
            try {
                return applyAsIntWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
