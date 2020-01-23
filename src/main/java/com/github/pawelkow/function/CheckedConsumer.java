package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<T, X extends Exception> extends Consumer<T>, Checked.WithNoValue<Consumer<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedConsumer<T, X> wrap(CheckedConsumer<? super T, ? extends X> consumer) {
        return (CheckedConsumer<T, X>) consumer;
    }

    void acceptWithException(T t) throws X;

    @Override
    default void accept(T t) {
        handleException(RETHROW_UNCHECKED).accept(t);
    }

    @Override
    default Consumer<T> handleException(VoidResolver handler) {
        return t -> {
            try {
                acceptWithException(t);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
