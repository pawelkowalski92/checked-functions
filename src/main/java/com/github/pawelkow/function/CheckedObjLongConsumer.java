package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.ObjLongConsumer;

/**
 * Extension for standard {@link ObjLongConsumer} that supports checked exceptions.
 *
 * @param <T> the type of the object argument to the operation
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedObjLongConsumer<T, X extends Exception> extends ObjLongConsumer<T>, Checked.WithNoValue<ObjLongConsumer<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  longStream.collect(ArrayList::new, wrap(ArrayList::add).discardException(), ArrayList::addAll);
     * </pre>
     *
     * @param objLongConsumer {@link CheckedObjLongConsumer} to be wrapped
     * @param <T>             the type of the object argument to the operation
     * @param <X>             {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedObjLongConsumer<T, X> wrap(CheckedObjLongConsumer<? super T, ? extends X> objLongConsumer) {
        return (CheckedObjLongConsumer<T, X>) objLongConsumer;
    }

    /**
     * Performs this operation on given arguments.
     *
     * @param t     first input argument
     * @param value second input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(T t, long value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(T t, long value) {
        handleException(RETHROW_UNCHECKED).accept(t, value);
    }

    @Override
    default ObjLongConsumer<T> handleException(VoidResolver handler) {
        return (t, value) -> {
            try {
                acceptWithException(t, value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
