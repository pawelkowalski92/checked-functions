package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.DoublePredicate;

@FunctionalInterface
public interface CheckedDoublePredicate<X extends Exception> extends DoublePredicate, Checked.WithBoolean<DoublePredicate, X> {

    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoublePredicate<X> wrap(CheckedDoublePredicate<? extends X> doublePredicate) {
        return (CheckedDoublePredicate<X>) doublePredicate;
    }

    boolean testWithException(double value) throws X;

    @Override
    default boolean test(double value) {
        return handleException(RETHROW_UNCHECKED).test(value);
    }

    @Override
    default DoublePredicate handleException(BooleanResolver handler) {
        return value -> {
            try {
                return testWithException(value);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
