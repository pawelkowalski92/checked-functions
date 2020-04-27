package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.LongConsumer;

/**
 * Extension for standard {@link LongConsumer} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongConsumer<X extends Exception> extends LongConsumer, Checked.WithNoValue<LongConsumer, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public void handle(long value) throws X {
     *      ...
     *  }
     *  optionalLong.ifPresent(wrap(this::handle).discardException());
     * </pre>
     *
     * @param longConsumer {@link CheckedLongConsumer} to be wrapped
     * @param <X>          {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongConsumer<X> wrap(CheckedLongConsumer<? extends X> longConsumer) {
        return (CheckedLongConsumer<X>) longConsumer;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param value the input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(long value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(long value) {
        handleException(RETHROW_UNCHECKED).accept(value);
    }

    @Override
    default LongConsumer handleException(VoidResolver handler) {
        return value -> {
            try {
                acceptWithException(value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
