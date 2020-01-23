package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.ToIntFunction;

@FunctionalInterface
public interface CheckedToIntFunction<T, X extends Exception> extends ToIntFunction<T>, Checked.WithInt<ToIntFunction<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedToIntFunction<T, X> wrap(CheckedToIntFunction<? super T, ? extends X> function) {
        return (CheckedToIntFunction<T, X>) function;
    }

    int applyAsIntWithException(T t) throws X;

    @Override
    default int applyAsInt(T t) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(t);
    }

    @Override
    default ToIntFunction<T> handleException(IntResolver handler) {
        return t -> {
            try {
                return applyAsIntWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
