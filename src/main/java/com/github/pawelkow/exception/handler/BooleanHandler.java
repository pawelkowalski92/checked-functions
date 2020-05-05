package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Specialized version of {@link ExceptionHandler} that provides {@link BooleanResolver}.
 */
public class BooleanHandler extends ExceptionHandler<BooleanResolver> implements BooleanResolver {

    @Override
    public boolean resolve(Throwable exception) {
        return getResolver(exception).resolve(exception);
    }

    /**
     * Get configurer for exception type expressed with type parameter.
     *
     * @param <X> the type of exception to be resolved
     * @return {@link BooleanHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf() {
        return new Configurer<>();
    }

    /**
     * Get configurer for exception type expressed with method parameter.
     *
     * @param exceptionType the type of exception to be resolved
     * @param <X>           the type of exception configurer should assume for resolution
     * @return {@link BooleanHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X> exceptionType) {
        return new Configurer<X>().forType(exceptionType);
    }

    /**
     * Get configurer for any {@link Throwable}.
     *
     * @return {@link BooleanHandler.Configurer configurer}
     */
    public Configurer<Throwable> inAnyCase() {
        return inCaseOf(Throwable.class);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link BooleanHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Collection<Class<? extends X>> exceptionTypes) {
        return new Configurer<X>().forTypes(exceptionTypes);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link BooleanHandler.Configurer configurer}
     */
    @SafeVarargs
    public final <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X>... exceptionTypes) {
        return inCaseOf(Arrays.asList(exceptionTypes));
    }

    /**
     * Specialized version of {@link ExceptionHandler.Configurer} that provides configurer for {@link BooleanHandler}.
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
         * Register new {@link BooleanResolver resolver} configured to map exception to a boolean primitive.
         *
         * @param exceptionMapper {@link Predicate} responsible for mapping exception to a boolean value
         * @return {@link BooleanHandler} for further configuration
         */
        public BooleanHandler mapToBoolean(Predicate<? super X> exceptionMapper) {
            registerResolver(new BooleanResolver() {
                @Override
                public boolean isSupported(Throwable exception) {
                    return Configurer.this.isConfiguredFor(exception);
                }

                @Override
                @SuppressWarnings("unchecked")
                public boolean resolve(Throwable exception) {
                    return exceptionMapper.test(intermediateAction.apply((X) exception));
                }
            });
            return BooleanHandler.this;
        }

        /**
         * Register new {@link BooleanResolver resolver} configured to rethrow exception as unchecked one.
         *
         * @param exceptionMapper {@link Function} responsible for translating checked exception to unchecked
         * @return {@link BooleanHandler} for further configuration
         */
        public BooleanHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToBoolean(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        /**
         * Register new {@link BooleanResolver resolver} configured to ignore the exception and supply a boolean primitive.
         *
         * @param defaultSupplier {@link BooleanSupplier} responsible for supplying a boolean value
         * @return {@link BooleanHandler} for further configuration
         */
        public BooleanHandler supplyBoolean(BooleanSupplier defaultSupplier) {
            return mapToBoolean(ex -> defaultSupplier.getAsBoolean());
        }

        /**
         * Register new {@link BooleanResolver resolver} configured to ignore the exception and return a boolean primitive.
         *
         * @param defaultValue provided boolean value
         * @return {@link BooleanHandler} for further configuration
         */
        public BooleanHandler returnBoolean(boolean defaultValue) {
            return supplyBoolean(() -> defaultValue);
        }

        /**
         * Register new {@link BooleanResolver resolver} configured to ignore the exception and return {@code false}.
         *
         * @return {@link BooleanHandler} for further configuration
         */
        public BooleanHandler discard() {
            return returnBoolean(false);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }
}
