package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface CheckedToDoubleFunction<T, X extends Exception> extends ToDoubleFunction<T>, Checked.WithDouble<ToDoubleFunction<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedToDoubleFunction<T, X> wrap(CheckedToDoubleFunction<? super T, ? extends X> function) {
        return (CheckedToDoubleFunction<T, X>) function;
    }

    double applyAsDoubleWithException(T t) throws X;

    @Override
    default double applyAsDouble(T t) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(t);
    }

    @Override
    default ToDoubleFunction<T> handleException(DoubleResolver handler) {
        return t -> {
            try {
                return applyAsDoubleWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
