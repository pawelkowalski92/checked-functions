package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.ObjLongConsumer;

@FunctionalInterface
public interface CheckedObjLongConsumer<T, X extends Exception> extends ObjLongConsumer<T>, Checked.WithNoValue<ObjLongConsumer<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedObjLongConsumer<T, X> wrap(CheckedObjLongConsumer<? super T, ? extends X> consumer) {
        return (CheckedObjLongConsumer<T, X>) consumer;
    }

    void acceptWithException(T t, long value) throws X;

    @Override
    default void accept(T t, long value) {
        handleException(RETHROW_UNCHECKED).accept(t, value);
    }

    @Override
    default ObjLongConsumer<T> handleException(VoidResolver handler) {
        return (t, value) -> {
            try {
                acceptWithException(t, value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
