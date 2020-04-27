package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.function.IntBinaryOperator;

/**
 * Extension for standard {@link IntBinaryOperator} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedIntBinaryOperator<X extends Exception> extends IntBinaryOperator, Checked.WithInt<IntBinaryOperator, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public int reduce(int left, int right) throws X {
     *      ...
     *  }
     *  intStream.reduce(0, wrap(this::reduce).returnFallback(0));
     * </pre>
     *
     * @param intBinaryOperator {@link CheckedIntBinaryOperator} to be wrapped
     * @param <X>               {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedIntBinaryOperator<X> wrap(CheckedIntBinaryOperator<? extends X> intBinaryOperator) {
        return (CheckedIntBinaryOperator<X>) intBinaryOperator;
    }

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     * @throws X {@link Exception exception(s)} related to this function
     */
    int applyAsIntWithException(int left, int right) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default int applyAsInt(int left, int right) {
        return handleException(RETHROW_UNCHECKED).applyAsInt(left, right);
    }

    @Override
    default IntBinaryOperator handleException(IntResolver handler) {
        return (t, u) -> {
            try {
                return applyAsIntWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
