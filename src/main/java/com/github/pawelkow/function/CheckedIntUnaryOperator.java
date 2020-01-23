package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface CheckedIntUnaryOperator<X extends Exception> extends IntUnaryOperator, Checked.WithInt<IntUnaryOperator, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntUnaryOperator<X> wrap(CheckedIntUnaryOperator<? extends X> binaryOperator) {
        return (CheckedIntUnaryOperator<X>) binaryOperator;
    }

    int applyAsIntWithException(int operand) throws X;

    @Override
    default int applyAsInt(int operand) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(operand);
    }

    @Override
    default IntUnaryOperator handleException(IntResolver handler) {
        return operand -> {
            try {
                return applyAsIntWithException(operand);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
