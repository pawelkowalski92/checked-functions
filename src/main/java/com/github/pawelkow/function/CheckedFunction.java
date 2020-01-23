package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.Function;

@FunctionalInterface
public interface CheckedFunction<T, R, X extends Exception> extends Function<T, R>, Checked.WithValue<R, Function<T, R>, X> {

    @SuppressWarnings("unchecked")
    static <T, R, X extends Exception> CheckedFunction<T, R, X> wrap(CheckedFunction<? super T, ? extends R, ? extends X> function) {
        return (CheckedFunction<T, R, X>) function;
    }

    R applyWithException(T t) throws X;

    @Override
    default R apply(T t) {
        return handleException(RETHROW_UNCHECKED).apply(t);
    }

    @Override
    default Function<T, R> handleException(ReferenceResolver<R> handler) {
        return t -> {
            try {
                return applyWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
