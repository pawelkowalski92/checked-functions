package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface CheckedLongToIntFunction<X extends Exception> extends LongToIntFunction, Checked.WithInt<LongToIntFunction, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongToIntFunction<X> wrap(CheckedLongToIntFunction<? extends X> function) {
        return (CheckedLongToIntFunction<X>) function;
    }

    int applyAsIntWithException(long value) throws X;

    @Override
    default int applyAsInt(long value) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(value);
    }

    @Override
    default LongToIntFunction handleException(IntResolver handler) {
        return value -> {
            try {
                return applyAsIntWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
