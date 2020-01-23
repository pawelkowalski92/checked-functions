package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.IntConsumer;

@FunctionalInterface
public interface CheckedIntConsumer<X extends Exception> extends IntConsumer, Checked.WithNoValue<IntConsumer, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntConsumer<X> wrap(CheckedIntConsumer<? extends X> consumer) {
        return (CheckedIntConsumer<X>) consumer;
    }

    void acceptWithException(int value) throws X;

    @Override
    default void accept(int value) {
        handleException(RETHROW_UNCHECKED).accept(value);
    }

    @Override
    default IntConsumer handleException(VoidResolver handler) {
        return value -> {
            try {
                acceptWithException(value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
