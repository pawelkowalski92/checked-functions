package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Specialized version of {@link ExceptionHandler} that provides {@link ReferenceResolver}.
 *
 * @param <R> the type of object reference to be returned
 */
public class ReferenceHandler<R> extends ExceptionHandler<ReferenceResolver<R>> implements ReferenceResolver<R> {

    @Override
    public R resolve(Throwable exception) {
        return getResolver(exception).resolve(exception);
    }

    /**
     * Get configurer for exception type expressed with type parameter.
     *
     * @param <X> the type of exception to be resolved
     * @return {@link ReferenceHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf() {
        return new Configurer<>();
    }

    /**
     * Get configurer for exception type expressed with method parameter.
     *
     * @param exceptionType the type of exception to be resolved
     * @param <X>           the type of exception configurer should assume for resolution
     * @return {@link ReferenceHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X> exceptionType) {
        return new Configurer<X>().forType(exceptionType);
    }

    /**
     * Get configurer for any {@link Throwable}.
     *
     * @return {@link ReferenceHandler.Configurer configurer}
     */
    public Configurer<Throwable> inAnyCase() {
        return inCaseOf(Throwable.class);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link ReferenceHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Collection<Class<? extends X>> exceptionTypes) {
        return new Configurer<X>().forTypes(exceptionTypes);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link ReferenceHandler.Configurer configurer}
     */
    @SafeVarargs
    public final <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X>... exceptionTypes) {
        return inCaseOf(Arrays.asList(exceptionTypes));
    }

    /**
     * Specialized version of {@link ExceptionHandler.Configurer} that provides configurer for {@link ReferenceHandler}.
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
         * Register new {@link ReferenceResolver resolver} configured to map exception to an object reference.
         *
         * @param exceptionMapper {@link Predicate} responsible for mapping exception to an object value
         * @return {@link ReferenceHandler} for further configuration
         */
        public ReferenceHandler<R> mapToValue(Function<? super X, ? extends R> exceptionMapper) {
            registerResolver(new ReferenceResolver<>() {
                @Override
                public boolean isSupported(Throwable exception) {
                    return Configurer.this.isConfiguredFor(exception);
                }

                @Override
                @SuppressWarnings("unchecked")
                public R resolve(Throwable exception) {
                    return intermediateAction.andThen(exceptionMapper).apply((X) exception);
                }
            });
            return ReferenceHandler.this;
        }

        /**
         * Register new {@link ReferenceResolver resolver} configured to rethrow exception as unchecked one.
         *
         * @param exceptionMapper {@link Function} responsible for translating checked exception to unchecked
         * @return {@link ReferenceHandler} for further configuration
         */
        public ReferenceHandler<R> rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToValue(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        /**
         * Register new {@link ReferenceResolver resolver} configured to ignore the exception and supply an object reference.
         *
         * @param defaultSupplier {@link Supplier} responsible for supplying an object value
         * @return {@link ReferenceHandler} for further configuration
         */
        public ReferenceHandler<R> supplyValue(Supplier<? extends R> defaultSupplier) {
            return mapToValue(ex -> defaultSupplier.get());
        }

        /**
         * Register new {@link ReferenceResolver resolver} configured to ignore the exception and return an object reference.
         *
         * @param defaultValue provided object value
         * @return {@link ReferenceHandler} for further configuration
         */
        public ReferenceHandler<R> returnValue(R defaultValue) {
            return supplyValue(() -> defaultValue);
        }

        /**
         * Register new {@link ReferenceResolver resolver} configured to ignore the exception and return {@code null}.
         *
         * @return {@link ReferenceHandler} for further configuration
         */
        public ReferenceHandler<R> discard() {
            return returnValue(null);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }

}
