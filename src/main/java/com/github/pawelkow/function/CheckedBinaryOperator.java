package com.github.pawelkow.function;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface CheckedBinaryOperator<T, X extends Exception> extends BinaryOperator<T>, CheckedBiFunction<T, T, T, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedBinaryOperator<T, X> wrap(CheckedBinaryOperator<T, ? extends X> binaryOperator) {
        return (CheckedBinaryOperator<T, X>) binaryOperator;
    }

}
