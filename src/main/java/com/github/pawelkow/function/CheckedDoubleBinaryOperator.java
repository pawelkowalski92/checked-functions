package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.DoubleBinaryOperator;

@FunctionalInterface
public interface CheckedDoubleBinaryOperator<X extends Exception> extends DoubleBinaryOperator, Checked.WithDouble<DoubleBinaryOperator, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleBinaryOperator<X> wrap(CheckedDoubleBinaryOperator<? extends X> binaryOperator) {
        return (CheckedDoubleBinaryOperator<X>) binaryOperator;
    }

    double applyAsDoubleWithException(double left, double right) throws X;

    @Override
    default double applyAsDouble(double left, double right) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(left, right);
    }

    @Override
    default DoubleBinaryOperator handleException(DoubleResolver handler) {
        return (t, u) -> {
            try {
                return applyAsDoubleWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
