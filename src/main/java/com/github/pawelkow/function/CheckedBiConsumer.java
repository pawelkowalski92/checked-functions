package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface CheckedBiConsumer<T, U, X extends Exception> extends BiConsumer<T, U>, Checked.WithNoValue<BiConsumer<T, U>, X> {

    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedBiConsumer<T, U, X> wrap(CheckedBiConsumer<? super T, ? super U, ? extends X> biConsumer) {
        return (CheckedBiConsumer<T, U, X>) biConsumer;
    }

    void acceptWithException(T t, U u) throws X;

    @Override
    default void accept(T t, U u) {
        handleException(RETHROW_UNCHECKED).accept(t, u);
    }

    @Override
    default BiConsumer<T, U> handleException(VoidResolver handler) {
        return (t, u) -> {
            try {
                acceptWithException(t, u);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
