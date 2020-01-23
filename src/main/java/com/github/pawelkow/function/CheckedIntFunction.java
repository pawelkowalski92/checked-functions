package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.IntFunction;

@FunctionalInterface
public interface CheckedIntFunction<R, X extends Exception> extends IntFunction<R>, Checked.WithValue<R, IntFunction<R>, X> {

    @SuppressWarnings("unchecked")
    static <R, X extends Exception> CheckedIntFunction<R, X> wrap(CheckedIntFunction<? extends R, ? extends X> intFunction) {
        return (CheckedIntFunction<R, X>) intFunction;
    }

    R applyWithException(int value) throws X;

    @Override
    default R apply(int value) {
        return handleException(RETHROW_UNCHECKED).apply(value);
    }

    @Override
    default IntFunction<R> handleException(ReferenceResolver<R> handler) {
        return value -> {
            try {
                return applyWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
