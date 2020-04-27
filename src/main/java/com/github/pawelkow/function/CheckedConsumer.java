package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.Consumer;

/**
 * Extension for standard {@link Consumer} that supports checked exceptions.
 *
 * @param <T> the type of the input to the operation
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedConsumer<T, X extends Exception> extends Consumer<T>, Checked.WithNoValue<Consumer<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public void handle(T t) throws X {
     *      ...
     *  }
     *  list.forEach(wrap(this::handle).discardException());
     * </pre>
     *
     * @param consumer {@link CheckedConsumer} to be wrapped
     * @param <T>      the type of the input to the operation
     * @param <X>      {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedConsumer<T, X> wrap(CheckedConsumer<? super T, ? extends X> consumer) {
        return (CheckedConsumer<T, X>) consumer;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(T t) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(T t) {
        handleException(RETHROW_UNCHECKED).accept(t);
    }

    @Override
    default Consumer<T> handleException(VoidResolver handler) {
        return t -> {
            try {
                acceptWithException(t);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
