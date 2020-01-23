package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.ToLongFunction;

@FunctionalInterface
public interface CheckedToLongFunction<T, X extends Exception> extends ToLongFunction<T>, Checked.WithLong<ToLongFunction<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedToLongFunction<T, X> wrap(CheckedToLongFunction<? super T, ? extends X> function) {
        return (CheckedToLongFunction<T, X>) function;
    }

    long applyAsLongWithException(T t) throws X;

    @Override
    default long applyAsLong(T t) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(t);
    }

    @Override
    default ToLongFunction<T> handleException(LongResolver handler) {
        return t -> {
            try {
                return applyAsLongWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
