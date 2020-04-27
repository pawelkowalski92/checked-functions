package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.DoubleConsumer;

/**
 * Extension for standard {@link DoubleConsumer} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoubleConsumer<X extends Exception> extends DoubleConsumer, Checked.WithNoValue<DoubleConsumer, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public void handle(double value) throws X {
     *      ...
     *  }
     *  optionalDouble.ifPresent(wrap(this::handle).discardException());
     * </pre>
     *
     * @param doubleConsumer {@link CheckedDoubleConsumer} to be wrapped
     * @param <X>            {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleConsumer<X> wrap(CheckedDoubleConsumer<? extends X> doubleConsumer) {
        return (CheckedDoubleConsumer<X>) doubleConsumer;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param value the input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(double value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(double value) {
        handleException(RETHROW_UNCHECKED).accept(value);
    }

    @Override
    default DoubleConsumer handleException(VoidResolver handler) {
        return value -> {
            try {
                acceptWithException(value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
