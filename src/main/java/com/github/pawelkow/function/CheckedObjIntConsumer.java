package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.ObjIntConsumer;

@FunctionalInterface
public interface CheckedObjIntConsumer<T, X extends Exception> extends ObjIntConsumer<T>, Checked.WithNoValue<ObjIntConsumer<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedObjIntConsumer<T, X> wrap(CheckedObjIntConsumer<? super T, ? extends X> consumer) {
        return (CheckedObjIntConsumer<T, X>) consumer;
    }

    void acceptWithException(T t, int value) throws X;

    @Override
    default void accept(T t, int value) {
        handleException(RETHROW_UNCHECKED).accept(t, value);
    }

    @Override
    default ObjIntConsumer<T> handleException(VoidResolver handler) {
        return (t, value) -> {
            try {
                acceptWithException(t, value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
