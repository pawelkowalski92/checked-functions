package com.github.pawelkow.function;

import java.util.function.UnaryOperator;

/**
 * Extension for standard {@link UnaryOperator} that supports checked exceptions.
 *
 * @param <T> the type of the operand and result of the operator
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedUnaryOperator<T, X extends Exception> extends UnaryOperator<T>, CheckedFunction<T, T, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public T transform(T t) throws X {
     *      ...
     *  }
     *  public T fallback() {
     *      ...
     *  }
     *  list.replaceAll(wrap(this::handle).supplyValue(this::fallback));
     * </pre>
     *
     * @param unaryOperator {@link CheckedUnaryOperator} to be wrapped
     * @param <T>           the type of the operand and result of the operator
     * @param <X>           {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedUnaryOperator<T, X> wrap(CheckedUnaryOperator<T, ? extends X> unaryOperator) {
        return (CheckedUnaryOperator<T, X>) unaryOperator;
    }

}
