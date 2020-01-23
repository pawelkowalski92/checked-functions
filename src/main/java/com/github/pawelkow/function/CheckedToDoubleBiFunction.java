package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.ToDoubleBiFunction;

@FunctionalInterface
public interface CheckedToDoubleBiFunction<T, U, X extends Exception> extends ToDoubleBiFunction<T, U>, Checked.WithDouble<ToDoubleBiFunction<T, U>, X> {

    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedToDoubleBiFunction<T, U, X> wrap(CheckedToDoubleBiFunction<? super T, ? super U, ? extends X> biFunction) {
        return (CheckedToDoubleBiFunction<T, U, X>) biFunction;
    }

    double applyAsDoubleWithException(T t, U u) throws X;

    @Override
    default double applyAsDouble(T t, U u) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(t, u);
    }

    @Override
    default ToDoubleBiFunction<T, U> handleException(DoubleResolver handler) {
        return (t, u) -> {
            try {
                return applyAsDoubleWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
