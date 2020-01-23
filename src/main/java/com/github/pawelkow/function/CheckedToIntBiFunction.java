package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.ToIntBiFunction;

@FunctionalInterface
public interface CheckedToIntBiFunction<T, U, X extends Exception> extends ToIntBiFunction<T, U>, Checked.WithInt<ToIntBiFunction<T, U>, X> {

    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedToIntBiFunction<T, U, X> wrap(CheckedToIntBiFunction<? super T, ? super U, ? extends X> biFunction) {
        return (CheckedToIntBiFunction<T, U, X>) biFunction;
    }

    int applyAsIntWithException(T t, U u) throws X;

    @Override
    default int applyAsInt(T t, U u) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(t, u);
    }

    @Override
    default ToIntBiFunction<T, U> handleException(IntResolver handler) {
        return (t, u) -> {
            try {
                return applyAsIntWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
