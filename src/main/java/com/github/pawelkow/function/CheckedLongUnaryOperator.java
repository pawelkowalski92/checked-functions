package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.LongUnaryOperator;

/**
 * Extension for standard {@link LongUnaryOperator} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongUnaryOperator<X extends Exception> extends LongUnaryOperator, Checked.WithLong<LongUnaryOperator, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public long transform(long value) throws X {
     *      ...
     *  }
     *  longStream.map(wrap(this::transform).returnFallback(-1L));
     * </pre>
     *
     * @param longUnaryOperator {@link CheckedLongUnaryOperator} to be wrapped
     * @param <X>               {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongUnaryOperator<X> wrap(CheckedLongUnaryOperator<? extends X> longUnaryOperator) {
        return (CheckedLongUnaryOperator<X>) longUnaryOperator;
    }

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     * @throws X {@link Exception exception(s)} related to this function
     */
    long applyAsLongWithException(long operand) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default long applyAsLong(long operand) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(operand);
    }

    @Override
    default LongUnaryOperator handleException(LongResolver handler) {
        return operand -> {
            try {
                return applyAsLongWithException(operand);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
