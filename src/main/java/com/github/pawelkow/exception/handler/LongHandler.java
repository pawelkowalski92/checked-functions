package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

/**
 * Specialized version of {@link ExceptionHandler} that provides {@link LongResolver}.
 */
public class LongHandler extends ExceptionHandler<LongResolver> implements LongResolver {

    @Override
    public long resolve(Throwable exception) {
        return getResolver(exception).resolve(exception);
    }

    /**
     * Get configurer for exception type expressed with type parameter.
     *
     * @param <X> the type of exception to be resolved
     * @return {@link LongHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOfGeneric() {
        return new Configurer<>();
    }

    /**
     * Get configurer for exception type expressed with method parameter.
     *
     * @param exceptionType the type of exception to be resolved
     * @param <X>           the type of exception configurer should assume for resolution
     * @return {@link LongHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X> exceptionType) {
        return new Configurer<X>().forType(exceptionType);
    }

    /**
     * Get configurer for any {@link Throwable}.
     *
     * @return {@link LongHandler.Configurer configurer}
     */
    public Configurer<Throwable> ifNoneMatch() {
        return inCaseOf(Throwable.class);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link LongHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Collection<Class<? extends X>> exceptionTypes) {
        return new Configurer<X>().forTypes(exceptionTypes);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link LongHandler.Configurer configurer}
     */
    @SafeVarargs
    public final <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X>... exceptionTypes) {
        return inCaseOf(Arrays.asList(exceptionTypes));
    }

    /**
     * Specialized version of {@link ExceptionHandler.Configurer} that provides configurer for {@link LongHandler}.
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
         * Register new {@link LongResolver resolver} configured to map exception to a long primitive.
         *
         * @param exceptionMapper {@link Predicate} responsible for mapping exception to a long value
         * @return {@link LongHandler} for further configuration
         */
        public LongHandler mapToLong(ToLongFunction<? super X> exceptionMapper) {
            registerResolver(new LongResolver() {
                @Override
                public boolean isSupported(Throwable exception) {
                    return LongHandler.Configurer.this.isConfiguredFor(exception);
                }

                @Override
                @SuppressWarnings("unchecked")
                public long resolve(Throwable exception) {
                    return exceptionMapper.applyAsLong(intermediateAction.apply((X) exception));
                }
            });
            return LongHandler.this;
        }

        /**
         * Register new {@link LongResolver resolver} configured to rethrow exception as unchecked one.
         *
         * @param exceptionMapper {@link Function} responsible for translating checked exception to unchecked
         * @return {@link LongHandler} for further configuration
         */
        public LongHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToLong(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        /**
         * Register new {@link LongResolver resolver} configured to ignore the exception and supply a long primitive.
         *
         * @param defaultSupplier {@link LongSupplier} responsible for supplying a long value
         * @return {@link LongHandler} for further configuration
         */
        public LongHandler supplyLong(LongSupplier defaultSupplier) {
            return mapToLong(ex -> defaultSupplier.getAsLong());
        }

        /**
         * Register new {@link LongResolver resolver} configured to ignore the exception and return a long primitive.
         *
         * @param defaultValue provided long value
         * @return {@link LongHandler} for further configuration
         */
        public LongHandler returnLong(long defaultValue) {
            return supplyLong(() -> defaultValue);
        }

        /**
         * Register new {@link LongResolver resolver} configured to ignore the exception and return {@code 0L}.
         *
         * @return {@link LongHandler} for further configuration
         */
        public LongHandler discard() {
            return returnLong(0L);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }

}
