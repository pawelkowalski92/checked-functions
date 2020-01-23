package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.LongBinaryOperator;

@FunctionalInterface
public interface CheckedLongBinaryOperator<X extends Exception> extends LongBinaryOperator, Checked.WithLong<LongBinaryOperator, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongBinaryOperator<X> wrap(CheckedLongBinaryOperator<? extends X> binaryOperator) {
        return (CheckedLongBinaryOperator<X>) binaryOperator;
    }

    long applyAsLongWithException(long left, long right) throws X;

    @Override
    default long applyAsLong(long left, long right) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(left, right);
    }

    @Override
    default LongBinaryOperator handleException(LongResolver handler) {
        return (t, u) -> {
            try {
                return applyAsLongWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
