package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Specialized version of {@link ExceptionHandler} that provides {@link DoubleResolver}.
 */
public class DoubleHandler extends ExceptionHandler<DoubleResolver> implements DoubleResolver {

    @Override
    public double resolve(Throwable exception) {
        return getResolver(exception).resolve(exception);
    }

    /**
     * Get configurer for exception type expressed with type parameter.
     *
     * @param <X> the type of exception to be resolved
     * @return {@link DoubleHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOfGeneric() {
        return new Configurer<>();
    }

    /**
     * Get configurer for exception type expressed with method parameter.
     *
     * @param exceptionType the type of exception to be resolved
     * @param <X>           the type of exception configurer should assume for resolution
     * @return {@link DoubleHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X> exceptionType) {
        return new Configurer<X>().forType(exceptionType);
    }

    /**
     * Get configurer for any {@link Throwable}.
     *
     * @return {@link DoubleHandler.Configurer configurer}
     */
    public Configurer<Throwable> ifNoneMatch() {
        return inCaseOf(Throwable.class);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link DoubleHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Collection<Class<? extends X>> exceptionTypes) {
        return new Configurer<X>().forTypes(exceptionTypes);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link DoubleHandler.Configurer configurer}
     */
    @SafeVarargs
    public final <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X>... exceptionTypes) {
        return inCaseOf(Arrays.asList(exceptionTypes));
    }

    /**
     * Specialized version of {@link ExceptionHandler.Configurer} that provides configurer for {@link DoubleHandler}.
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
         * Register new {@link DoubleResolver resolver} configured to map exception to a double primitive.
         *
         * @param exceptionMapper {@link ToDoubleFunction} responsible for mapping exception to a double value
         * @return {@link DoubleHandler} for further configuration
         */
        public DoubleHandler mapToDouble(ToDoubleFunction<? super X> exceptionMapper) {
            registerResolver(new DoubleResolver() {
                @Override
                public boolean isSupported(Throwable exception) {
                    return Configurer.this.isConfiguredFor(exception);
                }

                @Override
                @SuppressWarnings("unchecked")
                public double resolve(Throwable exception) {
                    return exceptionMapper.applyAsDouble(intermediateAction.apply((X) exception));
                }
            });
            return DoubleHandler.this;
        }

        /**
         * Register new {@link DoubleResolver resolver} configured to rethrow exception as unchecked one.
         *
         * @param exceptionMapper {@link Function} responsible for translating checked exception to unchecked
         * @return {@link DoubleHandler} for further configuration
         */
        public DoubleHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToDouble(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        /**
         * Register new {@link DoubleResolver resolver} configured to ignore the exception and supply a double primitive.
         *
         * @param defaultSupplier {@link DoubleSupplier} responsible for supplying a double value
         * @return {@link DoubleHandler} for further configuration
         */
        public DoubleHandler supplyDouble(DoubleSupplier defaultSupplier) {
            return mapToDouble(ex -> defaultSupplier.getAsDouble());
        }

        /**
         * Register new {@link DoubleResolver resolver} configured to ignore the exception and return a double primitive.
         *
         * @param defaultValue provided double value
         * @return {@link DoubleHandler} for further configuration
         */
        public DoubleHandler returnDouble(double defaultValue) {
            return supplyDouble(() -> defaultValue);
        }

        /**
         * Register new {@link DoubleResolver resolver} configured to ignore the exception and return {@code Double.NaN}.
         *
         * @return {@link DoubleHandler} for further configuration
         */
        public DoubleHandler discard() {
            return returnDouble(Double.NaN);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }
}
