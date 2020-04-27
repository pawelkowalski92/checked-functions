package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.DoubleBinaryOperator;

/**
 * Extension for standard {@link DoubleBinaryOperator} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoubleBinaryOperator<X extends Exception> extends DoubleBinaryOperator, Checked.WithDouble<DoubleBinaryOperator, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public double reduce(double left, double right) throws X {
     *      ...
     *  }
     *  doubleStream.reduce(0d, wrap(this::reduce).returnFallback(0d));
     * </pre>
     *
     * @param doubleBinaryOperator {@link CheckedDoubleBinaryOperator} to be wrapped
     * @param <X>                  {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleBinaryOperator<X> wrap(CheckedDoubleBinaryOperator<? extends X> doubleBinaryOperator) {
        return (CheckedDoubleBinaryOperator<X>) doubleBinaryOperator;
    }

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     * @throws X {@link Exception exception(s)} related to this function
     */
    double applyAsDoubleWithException(double left, double right) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default double applyAsDouble(double left, double right) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(left, right);
    }

    @Override
    default DoubleBinaryOperator handleException(DoubleResolver handler) {
        return (t, u) -> {
            try {
                return applyAsDoubleWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
