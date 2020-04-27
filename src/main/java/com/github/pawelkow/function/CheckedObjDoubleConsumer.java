package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.function.ObjDoubleConsumer;

/**
 * Extension for standard {@link ObjDoubleConsumer} that supports checked exceptions.
 *
 * @param <T> the type of the object argument to the operation
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedObjDoubleConsumer<T, X extends Exception> extends ObjDoubleConsumer<T>, Checked.WithNoValue<ObjDoubleConsumer<T>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  doubleStream.collect(ArrayList::new, wrap(ArrayList::add).discardException(), ArrayList::addAll);
     * </pre>
     *
     * @param objDoubleConsumer {@link CheckedObjDoubleConsumer} to be wrapped
     * @param <T>               the type of the object argument to the operation
     * @param <X>               {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedObjDoubleConsumer<T, X> wrap(CheckedObjDoubleConsumer<? super T, ? extends X> objDoubleConsumer) {
        return (CheckedObjDoubleConsumer<T, X>) objDoubleConsumer;
    }

    /**
     * Performs this operation on given arguments.
     *
     * @param t     first input argument
     * @param value second input argument
     * @throws X {@link Exception exception(s)} related to this operation
     */
    void acceptWithException(T t, double value) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default void accept(T t, double value) {
        handleException(RETHROW_UNCHECKED).accept(t, value);
    }

    @Override
    default ObjDoubleConsumer<T> handleException(VoidResolver handler) {
        return (t, value) -> {
            try {
                acceptWithException(t, value);
            } catch (Exception exception) {
                handler.resolve(exception);
            }
        };
    }

}
