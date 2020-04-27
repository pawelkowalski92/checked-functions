package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.function.ToDoubleBiFunction;

/**
 * Extension for standard {@link ToDoubleBiFunction} that supports checked exceptions.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedToDoubleBiFunction<T, U, X extends Exception> extends ToDoubleBiFunction<T, U>, Checked.WithDouble<ToDoubleBiFunction<T, U>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public double reduce(T t, U u) throws X {
     *      ...
     *  }
     *
     *  concurrentHashMap.reduceToDouble(100l, wrap(this::reduce).returnFallback(0d), Double::sum);
     * </pre>
     *
     * @param toDoubleBiFunction {@link CheckedToDoubleBiFunction} to be wrapped
     * @param <T>                the type of the first argument to the function
     * @param <U>                the type of the second argument to the function
     * @param <X>                {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedToDoubleBiFunction<T, U, X> wrap(CheckedToDoubleBiFunction<? super T, ? super U, ? extends X> toDoubleBiFunction) {
        return (CheckedToDoubleBiFunction<T, U, X>) toDoubleBiFunction;
    }

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     * @throws X {@link Exception exception(s)} related to this function
     */
    double applyAsDoubleWithException(T t, U u) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default double applyAsDouble(T t, U u) {
        return handleException(RETHROW_UNCHECKED).applyAsDouble(t, u);
    }

    @Override
    default ToDoubleBiFunction<T, U> handleException(DoubleResolver handler) {
        return (t, u) -> {
            try {
                return applyAsDoubleWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
