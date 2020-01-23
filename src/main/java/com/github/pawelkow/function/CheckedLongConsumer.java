package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.LongConsumer;

@FunctionalInterface
public interface CheckedLongConsumer<X extends Exception> extends LongConsumer, Checked.WithNoValue<LongConsumer, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongConsumer<X> wrap(CheckedLongConsumer<? extends X> consumer) {
        return (CheckedLongConsumer<X>) consumer;
    }

    void acceptWithException(long value) throws X;

    @Override
    default void accept(long value) {
        handleException(RETHROW_UNCHECKED).accept(value);
    }

    @Override
    default LongConsumer handleException(VoidResolver handler) {
        return value -> {
            try {
                acceptWithException(value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
