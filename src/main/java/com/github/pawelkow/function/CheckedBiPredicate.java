package com.github.pawelkow.function;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.function.BiPredicate;

/**
 * Extension for standard {@link BiPredicate} that supports checked exceptions.
 *
 * @param <T> the type of the first argument to the predicate
 * @param <U> the type of the second argument to the predicate
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
@FunctionalInterface
public interface CheckedBiPredicate<T, U, X extends Exception> extends BiPredicate<T, U>, Checked.WithBoolean<BiPredicate<T, U>, X> {

    /**
     * Utility function that enables support for method references, e.g.
     *
     * <pre>
     *  public static &lt;K, V&gt; copy(Map&lt;K, V&gt; map, BiPredicate&lt;? super K, ? super V&gt; preconditions) {
     *      ...
     *  }
     *  public boolean filter(T t, U u) throws X {
     *      ...
     *  }
     *  Map&lt;K, V&gt; newMap = copy(map, wrap(this::filter).returnBoolean(false));
     * </pre>
     *
     * @param biPredicate {@link CheckedBiPredicate} to be wrapped
     * @param <T>         the type of the first argument to the predicate
     * @param <U>         the type of the second argument to the predicate
     * @param <X>         {@link Exception exception} type that is supported
     * @return extended interface
     */
    @SuppressWarnings("unchecked")
    static <T, U, X extends Exception> CheckedBiPredicate<T, U, X> wrap(CheckedBiPredicate<? super T, ? super U, ? extends X> biPredicate) {
        return (CheckedBiPredicate<T, U, X>) biPredicate;
    }

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     * @throws X {@link Exception exception(s)} related to this predicate
     */
    boolean testWithException(T t, U u) throws X;

    /**
     * {@inheritDoc}
     * <p>
     * Any exception that could be thrown will be rethrown based on {@link Checked#RETHROW_UNCHECKED default strategy}.
     */
    @Override
    default boolean test(T t, U u) {
        return handleException(RETHROW_UNCHECKED).test(t, u);
    }

    @Override
    default BiPredicate<T, U> handleException(BooleanResolver handler) {
        return (t, u) -> {
            try {
                return testWithException(t, u);
            } catch (Exception exception) {
                return handler.resolve(exception);
            }
        };
    }

}
