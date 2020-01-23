package com.github.pawelkow.function;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface CheckedUnaryOperator<T, X extends Exception> extends UnaryOperator<T>, CheckedFunction<T, T, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedUnaryOperator<T, X> wrap(CheckedUnaryOperator<T, ? extends X> unaryOperator) {
        return (CheckedUnaryOperator<T, X>) unaryOperator;
    }

}
