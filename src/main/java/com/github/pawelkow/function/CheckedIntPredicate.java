package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.IntPredicate;

@FunctionalInterface
public interface CheckedIntPredicate<X extends Exception> extends IntPredicate, Checked.WithBoolean<IntPredicate, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntPredicate<X> wrap(CheckedIntPredicate<? extends X> intPredicate) {
        return (CheckedIntPredicate<X>) intPredicate;
    }

    boolean testWithException(int value) throws X;

    @Override
    default boolean test(int value) {
        return handleException(RETHROW_UNCHECKED).test(value);
    }

    @Override
    default IntPredicate handleException(BooleanResolver handler) {
        return value -> {
            try {
                return testWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
