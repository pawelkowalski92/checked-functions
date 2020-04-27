package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.function.LongBinaryOperator;

/**
 * Extension for standard {@link LongBinaryOperator} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedLongBinaryOperator<X extends Exception> extends LongBinaryOperator, Checked.WithLong<LongBinaryOperator, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public long reduce(long left, long right) throws X {
     *      ...
     *  }
     *  longStream.reduce(0l, wrap(this::reduce).returnFallback(-1l));
     * </pre>
     *
     * @param longBinaryOperator {@link CheckedLongBinaryOperator} to be wrapped
     * @param <X>                {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedLongBinaryOperator<X> wrap(CheckedLongBinaryOperator<? extends X> longBinaryOperator) {
        return (CheckedLongBinaryOperator<X>) longBinaryOperator;
    }

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     * @throws X {@link Exception exception(s)} related to this function
     */
    long applyAsLongWithException(long left, long right) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default long applyAsLong(long left, long right) {
        return handleException(RETHROW_UNCHECKED).applyAsLong(left, right);
    }

    @Override
    default LongBinaryOperator handleException(LongResolver handler) {
        return (t, u) -> {
            try {
                return applyAsLongWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
