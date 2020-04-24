package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.DoubleFunction;

@FunctionalInterface
public interface CheckedDoubleFunction<R, X extends Exception> extends DoubleFunction<R>, Checked.WithValue<R, DoubleFunction<R>, X> {

    @SuppressWarnings("unchecked")
    static <R, X extends Exception> CheckedDoubleFunction<R, X> wrap(CheckedDoubleFunction<? extends R, ? extends X> doubleFunction) {
        return (CheckedDoubleFunction<R, X>) doubleFunction;
    }

    R applyWithException(double value) throws X;

    @Override
    default R apply(double value) {
        return handleException(RETHROW_UNCHECKED).apply(value);
    }

    @Override
    default DoubleFunction<R> handleException(ReferenceResolver<? extends R> handler) {
        return value -> {
            try {
                return applyWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
