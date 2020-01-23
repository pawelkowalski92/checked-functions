package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.LongUnaryOperator;

@FunctionalInterface
public interface CheckedLongUnaryOperator<X extends Exception> extends LongUnaryOperator, Checked.WithLong<LongUnaryOperator, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongUnaryOperator<X> wrap(CheckedLongUnaryOperator<? extends X> unaryOperator) {
        return (CheckedLongUnaryOperator<X>) unaryOperator;
    }

    long applyAsLongWithException(long operand) throws X;

    @Override
    default long applyAsLong(long operand) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(operand);
    }

    @Override
    default LongUnaryOperator handleException(LongResolver handler) {
        return operand -> {
            try {
                return applyAsLongWithException(operand);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
