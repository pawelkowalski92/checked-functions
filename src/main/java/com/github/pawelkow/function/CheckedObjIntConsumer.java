package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.ObjIntConsumer;

/**
 * Extension for standard {@link ObjIntConsumer} that supports checked exceptions.
 *
 * @param <T> the type of the object argument to the operation
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedObjIntConsumer<T, X extends Exception> extends ObjIntConsumer<T>, Checked.WithNoValue<ObjIntConsumer<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  intStream.collect(ArrayList::new, wrap(ArrayList::add).discardException(), ArrayList::addAll);
     * </pre>
     *
     * @param objIntConsumer {@link CheckedObjIntConsumer} to be wrapped
     * @param <T>            the type of the object argument to the operation
     * @param <X>            {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedObjIntConsumer<T, X> wrap(CheckedObjIntConsumer<? super T, ? extends X> objIntConsumer) {
        return (CheckedObjIntConsumer<T, X>) objIntConsumer;
    }

    /**
     * Performs this operation on given arguments.
     *
     * @param t     first input argument
     * @param value second input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(T t, int value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(T t, int value) {
        handleException(RETHROW_UNCHECKED).accept(t, value);
    }

    @Override
    default ObjIntConsumer<T> handleException(VoidResolver handler) {
        return (t, value) -> {
            try {
                acceptWithException(t, value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
