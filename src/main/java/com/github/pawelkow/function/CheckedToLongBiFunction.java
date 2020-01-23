package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.ToLongBiFunction;

@FunctionalInterface
public interface CheckedToLongBiFunction<T, U, X extends Exception> extends ToLongBiFunction<T, U>, Checked.WithLong<ToLongBiFunction<T, U>, X> {

    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedToLongBiFunction<T, U, X> wrap(CheckedToLongBiFunction<? super T, ? super U, ? extends X> biFunction) {
        return (CheckedToLongBiFunction<T, U, X>) biFunction;
    }

    long applyAsLongWithException(T t, U u) throws X;

    @Override
    default long applyAsLong(T t, U u) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(t, u);
    }

    @Override
    default ToLongBiFunction<T, U> handleException(LongResolver handler) {
        return (t, u) -> {
            try {
                return applyAsLongWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
