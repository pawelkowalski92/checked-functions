package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface CheckedDoubleConsumer<X extends Exception> extends DoubleConsumer, Checked.WithNoValue<DoubleConsumer, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleConsumer<X> wrap(CheckedDoubleConsumer<? extends X> consumer) {
        return (CheckedDoubleConsumer<X>) consumer;
    }

    void acceptWithException(double value) throws X;

    @Override
    default void accept(double value) {
        handleException(RETHROW_UNCHECKED).accept(value);
    }

    @Override
    default DoubleConsumer handleException(VoidResolver handler) {
        return value -> {
            try {
                acceptWithException(value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
