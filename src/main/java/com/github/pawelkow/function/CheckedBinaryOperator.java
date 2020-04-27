package com.github.pawelkow.function;

import java.util.function.BinaryOperator;

/**
 * Extension for standard {@link BinaryOperator} that supports checked exceptions.
 *
 * @param <T> the type of the operands and result of the operator
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedBinaryOperator<T, X extends Exception> extends BinaryOperator<T>, CheckedBiFunction<T, T, T, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public T transform(T t, T u) throws X {
     *      ...
     *  }
     *  public T fallback() {
     *      ...
     *  }
     *  map.forEach(wrap(this::handle).supplyValue(this::fallback));
     * </pre>
     *
     * @param binaryOperator {@link CheckedBinaryOperator} to be wrapped
     * @param <T>            the type of the operands and result of the operator
     * @param <X>            {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> CheckedBinaryOperator<T, X> wrap(CheckedBinaryOperator<T, ? extends X> binaryOperator) {
        return (CheckedBinaryOperator<T, X>) binaryOperator;
    }

}
