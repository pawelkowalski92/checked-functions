package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.BiConsumer;

/**
 * Extension for standard {@link BiConsumer} that supports checked exceptions.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U, X extends Exception> extends BiConsumer<T, U>, Checked.WithNoValue<BiConsumer<T, U>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public void handle(T t, U u) throws X {
     *      ...
     *  }
     *  map.forEach(wrap(this::handle).discardException());
     * </pre>
     *
     * @param biConsumer {@link CheckedBiConsumer} to be wrapped
     * @param <T>        the type of the first argument to the operation
     * @param <U>        the type of the second argument to the operation
     * @param <X>        {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedBiConsumer<T, U, X> wrap(CheckedBiConsumer<? super T, ? super U, ? extends X> biConsumer) {
        return (CheckedBiConsumer<T, U, X>) biConsumer;
    }

    /**
     * Performs this operation on given arguments.
     *
     * @param t first input argument
     * @param u second input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(T t, U u) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(T t, U u) {
        handleException(RETHROW_UNCHECKED).accept(t, u);
    }

    @Override
    default BiConsumer<T, U> handleException(VoidResolver handler) {
        return (t, u) -> {
            try {
                acceptWithException(t, u);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
