package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface CheckedBiPredicate<T, U, X extends Exception> extends BiPredicate<T, U>, Checked.WithBoolean<BiPredicate<T, U>, X> {

    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedBiPredicate<T, U, X> wrap(CheckedBiPredicate<? super T, ? super U, ? extends X> biPredicate) {
        return (CheckedBiPredicate<T, U, X>) biPredicate;
    }

    boolean testWithException(T t, U u) throws X;

    @Override
    default boolean test(T t, U u) {
        return handleException(RETHROW_UNCHECKED).test(t, u);
    }

    @Override
    default BiPredicate<T, U> handleException(BooleanResolver handler) {
        return (t, u) -> {
            try {
                return testWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
