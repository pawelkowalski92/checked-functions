package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.ObjDoubleConsumer;

@FunctionalInterface
public interface CheckedObjDoubleConsumer<T, X extends Exception> extends ObjDoubleConsumer<T>, Checked.WithNoValue<ObjDoubleConsumer<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedObjDoubleConsumer<T, X> wrap(CheckedObjDoubleConsumer<? super T, ? extends X> consumer) {
        return (CheckedObjDoubleConsumer<T, X>) consumer;
    }

    void acceptWithException(T t, double value) throws X;

    @Override
    default void accept(T t, double value) {
        handleException(RETHROW_UNCHECKED).accept(t, value);
    }

    @Override
    default ObjDoubleConsumer<T> handleException(VoidResolver handler) {
        return (t, value) -> {
            try {
                acceptWithException(t, value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
