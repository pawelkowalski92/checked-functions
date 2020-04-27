package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.IntConsumer;

/**
 * Extension for standard {@link IntConsumer} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntConsumer<X extends Exception> extends IntConsumer, Checked.WithNoValue<IntConsumer, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public void handle(int value) throws X {
     *      ...
     *  }
     *  optionalInt.ifPresent(wrap(this::handle).discardException());
     * </pre>
     *
     * @param intConsumer {@link CheckedIntConsumer} to be wrapped
     * @param <X>         {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntConsumer<X> wrap(CheckedIntConsumer<? extends X> intConsumer) {
        return (CheckedIntConsumer<X>) intConsumer;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param value the input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(int value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(int value) {
        handleException(RETHROW_UNCHECKED).accept(value);
    }

    @Override
    default IntConsumer handleException(VoidResolver handler) {
        return value -> {
            try {
                acceptWithException(value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
