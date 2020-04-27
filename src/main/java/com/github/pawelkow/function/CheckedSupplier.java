package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.function.Supplier;

/**
 * Extension for standard {@link Supplier} that supports checked exceptions.
 *
 * @param <T> the type of results supplied by this supplier
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedSupplier<T, X extends Exception> extends Supplier<T>, Checked.WithValue<T, Supplier<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public T fallback() throws X {
     *      ...
     *  }
     *  optional.orElseGet(wrap(this::fallback).rethrowException(IllegalArgumentException::new));
     * </pre>
     *
     * @param supplier {@link CheckedSupplier} to be wrapped
     * @param <X>      {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedSupplier<T, X> wrap(CheckedSupplier<? extends T, ? extends X> supplier) {
        return (CheckedSupplier<T, X>) supplier;
    }

    /**
     * Gets a result.
     *
     * @return a result
     * @throws X {@link Exception exception(s)} related to this supplier
     */
    T getWithException() throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default T get() {
        return handleException(RETHROW_UNCHECKED).get();
    }

    @Override
    default Supplier<T> handleException(ReferenceResolver<? extends T> handler) {
        return () -> {
            try {
                return getWithException();
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
