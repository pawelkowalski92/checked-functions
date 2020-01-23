package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.LongPredicate;

@FunctionalInterface
public interface CheckedLongPredicate<X extends Exception> extends LongPredicate, Checked.WithBoolean<LongPredicate, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongPredicate<X> wrap(CheckedLongPredicate<? extends X> predicate) {
        return (CheckedLongPredicate<X>) predicate;
    }

    boolean testWithException(long value) throws X;

    @Override
    default boolean test(long value) {
        return handleException(RETHROW_UNCHECKED).test(value);
    }

    @Override
    default LongPredicate handleException(BooleanResolver handler) {
        return value -> {
            try {
                return testWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
