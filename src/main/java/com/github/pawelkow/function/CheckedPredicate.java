package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.Predicate;

@FunctionalInterface
public interface CheckedPredicate<T, X extends Exception> extends Predicate<T>, Checked.WithBoolean<Predicate<T>, X> {

    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedPredicate<T, X> wrap(CheckedPredicate<? super T, ? extends X> predicate) {
        return (CheckedPredicate<T, X>) predicate;
    }

    boolean testWithException(T t) throws X;

    @Override
    default boolean test(T t) {
        return handleException(RETHROW_UNCHECKED).test(t);
    }

    @Override
    default Predicate<T> handleException(BooleanResolver handler) {
        return t -> {
            try {
                return testWithException(t);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
