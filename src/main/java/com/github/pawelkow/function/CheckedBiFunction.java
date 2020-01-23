package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.BiFunction;

@FunctionalInterface
public interface CheckedBiFunction<T, U, R, X extends Exception> extends BiFunction<T, U, R>, Checked.WithValue<R, BiFunction<T, U, R>, X> {

    @SuppressWarnings("unchecked")
    static <T, U, R, X extends Exception> CheckedBiFunction<T, U, R, X> wrap(CheckedBiFunction<? super T, ? super U, ? extends R, ? extends X> biFunction) {
        return (CheckedBiFunction<T, U, R, X>) biFunction;
    }

    R applyWithException(T t, U u) throws X;

    @Override
    default R apply(T t, U u) {
        return handleException(RETHROW_UNCHECKED).apply(t, u);
    }

    @Override
    default BiFunction<T, U, R> handleException(ReferenceResolver<R> handler) {
        return (t, u) -> {
            try {
                return applyWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
