package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.DoubleUnaryOperator;

/**
 * Extension for standard {@link DoubleUnaryOperator} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedDoubleUnaryOperator<X extends Exception> extends DoubleUnaryOperator, Checked.WithDouble<DoubleUnaryOperator, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public double transform(double value) throws X {
     *      ...
     *  }
     *  doubleStream.map(wrap(this::transform).returnFallback(Double.NaN));
     * </pre>
     *
     * @param doubleUnaryOperator {@link CheckedDoubleUnaryOperator} to be wrapped
     * @param <X>                 {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedDoubleUnaryOperator<X> wrap(CheckedDoubleUnaryOperator<? extends X> doubleUnaryOperator) {
        return (CheckedDoubleUnaryOperator<X>) doubleUnaryOperator;
    }

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     * @throws X {@link Exception exception(s)} related to this function
     */
    double applyAsDoubleWithException(double operand) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default double applyAsDouble(double operand) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(operand);
    }

    @Override
    default DoubleUnaryOperator handleException(DoubleResolver handler) {
        return operand -> {
            try {
                return applyAsDoubleWithException(operand);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
