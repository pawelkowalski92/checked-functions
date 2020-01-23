package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface CheckedDoubleUnaryOperator<X extends Exception> extends DoubleUnaryOperator, Checked.WithDouble<DoubleUnaryOperator, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleUnaryOperator<X> wrap(CheckedDoubleUnaryOperator<? extends X> binaryOperator) {
        return (CheckedDoubleUnaryOperator<X>) binaryOperator;
    }

    double applyAsDoubleWithException(double operand) throws X;

    @Override
    default double applyAsDouble(double operand) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(operand);
    }

    @Override
    default DoubleUnaryOperator handleException(DoubleResolver handler) {
        return operand -> {
            try {
                return applyAsDoubleWithException(operand);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
