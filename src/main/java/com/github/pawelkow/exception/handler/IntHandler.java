package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Specialized version of {@link ExceptionHandler} that provides {@link IntResolver}.
 */
public class IntHandler extends ExceptionHandler<IntResolver> implements IntResolver {

    @Override
    public int resolve(Throwable exception) {
        return getResolver(exception).resolve(exception);
    }

    /**
     * Get configurer for exception type expressed with type parameter.
     *
     * @param <X> the type of exception to be resolved
     * @return {@link IntHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf() {
        return new Configurer<>();
    }

    /**
     * Get configurer for exception type expressed with method parameter.
     *
     * @param exceptionType the type of exception to be resolved
     * @param <X>           the type of exception configurer should assume for resolution
     * @return {@link IntHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X> exceptionType) {
        return new Configurer<X>().forType(exceptionType);
    }

    /**
     * Get configurer for any {@link Throwable}.
     *
     * @return {@link IntHandler.Configurer configurer}
     */
    public Configurer<Throwable> inAnyCase() {
        return inCaseOf(Throwable.class);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link IntHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Collection<Class<? extends X>> exceptionTypes) {
        return new Configurer<X>().forTypes(exceptionTypes);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link IntHandler.Configurer configurer}
     */
    @SafeVarargs
    public final <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X>... exceptionTypes) {
        return inCaseOf(Arrays.asList(exceptionTypes));
    }

    /**
     * Specialized version of {@link ExceptionHandler.Configurer} that provides configurer for {@link IntHandler}.
     *
     * @param <X> the type of exception used for configuration
     */
    public class Configurer<X extends Throwable> extends ExceptionHandler.Configurer<X, Configurer<X>> {

        /**
         * Private constructor (instances should be created via methods available in outer class).
         */
        private Configurer() {
        }

        /**
         * Register new {@link IntResolver resolver} configured to map exception to a int primitive.
         *
         * @param exceptionMapper {@link Predicate} responsible for mapping exception to a int value
         * @return {@link IntHandler} for further configuration
         */
        public IntHandler mapToInt(ToIntFunction<? super X> exceptionMapper) {
            registerResolver(new IntResolver() {
                @Override
                public boolean isSupported(Throwable exception) {
                    return Configurer.this.isConfiguredFor(exception);
                }

                @Override
                @SuppressWarnings("unchecked")
                public int resolve(Throwable exception) {
                    return exceptionMapper.applyAsInt(intermediateAction.apply((X) exception));
                }
            });
            return IntHandler.this;
        }

        /**
         * Register new {@link IntResolver resolver} configured to rethrow exception as unchecked one.
         *
         * @param exceptionMapper {@link Function} responsible for translating checked exception to unchecked
         * @return {@link IntHandler} for further configuration
         */
        public IntHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToInt(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        /**
         * Register new {@link IntResolver resolver} configured to ignore the exception and supply a int primitive.
         *
         * @param defaultSupplier {@link IntSupplier} responsible for supplying a int value
         * @return {@link IntHandler} for further configuration
         */
        public IntHandler supplyInt(IntSupplier defaultSupplier) {
            return mapToInt(ex -> defaultSupplier.getAsInt());
        }

        /**
         * Register new {@link IntResolver resolver} configured to ignore the exception and return a int primitive.
         *
         * @param defaultValue provided int value
         * @return {@link IntHandler} for further configuration
         */
        public IntHandler returnInt(int defaultValue) {
            return supplyInt(() -> defaultValue);
        }

        /**
         * Register new {@link IntResolver resolver} configured to ignore the exception and return {@code 0}.
         *
         * @return {@link IntHandler} for further configuration
         */
        public IntHandler discard() {
            return returnInt(0);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }

}
