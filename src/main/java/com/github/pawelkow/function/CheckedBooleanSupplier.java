package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.BooleanSupplier;

/**
 * Extension for standard {@link BooleanSupplier} that supports checked exceptions.
 *
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedBooleanSupplier<X extends Exception> extends BooleanSupplier, Checked.WithBoolean<BooleanSupplier, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public static Runnable runUntil(BooleanSupplier runCondition) {
     *      while (runCondition.getAsBoolean()) {
     *          ...
     *      }
     *  }
     *  public boolean shouldRun() throws X {
     *      ...
     *  }
     *  Runnable runner = runUntil(wrap(this::shouldRun).returnFallback(false));
     *  new Thread(runner).start();
     * </pre>
     *
     * @param booleanSupplier {@link CheckedBooleanSupplier} to be wrapped
     * @param <X>             {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> CheckedBooleanSupplier<X> wrap(CheckedBooleanSupplier<? extends X> booleanSupplier) {
        return (CheckedBooleanSupplier<X>) booleanSupplier;
    }

    /**
     * Gets a result.
     *
     * @return a result
     * @throws X {@link Exception exception(s)} related to this supplier
     */
    boolean getAsBooleanWithException() throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default boolean getAsBoolean() {
        return handleException(RETHROW_UNCHECKED).getAsBoolean();
    }

    @Override
    default BooleanSupplier handleException(BooleanResolver handler) {
        return () -> {
            try {
                return getAsBooleanWithException();
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
