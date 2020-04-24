package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.LongFunction;

@FunctionalInterface
public interface CheckedLongFunction<R, X extends Exception> extends LongFunction<R>, Checked.WithValue<R, LongFunction<R>, X> {

    @SuppressWarnings("unchecked")
    static <R, X extends Exception> CheckedLongFunction<R, X> wrap(CheckedLongFunction<? extends R, ? extends X> function) {
        return (CheckedLongFunction<R, X>) function;
    }

    R applyWithException(long value) throws X;

    @Override
    default R apply(long value) {
        return handleException(RETHROW_UNCHECKED).apply(value);
    }

    @Override
    default LongFunction<R> handleException(ReferenceResolver<? extends R> handler) {
        return value -> {
            try {
                return applyWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
