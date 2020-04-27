package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.IntUnaryOperator;

/**
 * Extension for standard {@link IntUnaryOperator} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntUnaryOperator<X extends Exception> extends IntUnaryOperator, Checked.WithInt<IntUnaryOperator, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public int transform(int value) throws X {
     *      ...
     *  }
     *  intStream.map(wrap(this::transform).returnFallback(-1));
     * </pre>
     *
     * @param intUnaryOperator {@link CheckedIntUnaryOperator} to be wrapped
     * @param <X>              {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntUnaryOperator<X> wrap(CheckedIntUnaryOperator<? extends X> intUnaryOperator) {
        return (CheckedIntUnaryOperator<X>) intUnaryOperator;
    }

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     * @throws X {@link Exception exception(s)} related to this function
     */
    int applyAsIntWithException(int operand) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default int applyAsInt(int operand) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(operand);
    }

    @Override
    default IntUnaryOperator handleException(IntResolver handler) {
        return operand -> {
            try {
                return applyAsIntWithException(operand);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
