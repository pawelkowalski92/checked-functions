package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * Specialized version of {@link ExceptionHandler} that provides {@link VoidResolver}.
 */
public class VoidHandler extends ExceptionHandler<VoidResolver> implements VoidResolver {

    @Override
    public void resolve(Throwable exception) {
        getResolver(exception).resolve(exception);
    }

    /**
     * Get configurer for exception type expressed with type parameter.
     *
     * @param <X> the type of exception to be resolved
     * @return {@link VoidHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf() {
        return new Configurer<>();
    }

    /**
     * Get configurer for exception type expressed with method parameter.
     *
     * @param exceptionType the type of exception to be resolved
     * @param <X>           the type of exception configurer should assume for resolution
     * @return {@link VoidHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X> exceptionType) {
        return new Configurer<X>().forType(exceptionType);
    }

    /**
     * Get configurer for any {@link Throwable}.
     *
     * @return {@link VoidHandler.Configurer configurer}
     */
    public Configurer<Throwable> inAnyCase() {
        return inCaseOf(Throwable.class);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link VoidHandler.Configurer configurer}
     */
    public <X extends Throwable> Configurer<X> inCaseOf(Collection<Class<? extends X>> exceptionTypes) {
        return new Configurer<X>().forTypes(exceptionTypes);
    }

    /**
     * Get configurer for collection of exception types.
     *
     * @param exceptionTypes collection of the exception types to be resolved
     * @param <X>            the type of exception configurer should assume for resolution
     * @return {@link VoidHandler.Configurer configurer}
     */
    @SafeVarargs
    public final <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X>... exceptionTypes) {
        return inCaseOf(Arrays.asList(exceptionTypes));
    }

    /**
     * Specialized version of {@link ExceptionHandler.Configurer} that provides configurer for {@link VoidHandler}.
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
         * Register new {@link VoidResolver resolver} configured to rethrow exception as unchecked one.
         *
         * @param exceptionMapper {@link Function} responsible for translating checked exception to unchecked
         * @return {@link VoidHandler} for further configuration
         */
        public VoidHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handle(ex -> {
                throw exceptionMapper.apply(ex);
            }).discard();
        }

        /**
         * Register new {@link VoidResolver resolver} configured to ignore the exception.
         *
         * @return {@link VoidHandler} for further configuration
         */
        public VoidHandler discard() {
            registerResolver(new VoidResolver() {
                @Override
                public boolean isSupported(Throwable exception) {
                    return Configurer.this.isConfiguredFor(exception);
                }

                @Override
                @SuppressWarnings("unchecked")
                public void resolve(Throwable exception) {
                    intermediateAction.apply((X) exception);
                }
            });
            return VoidHandler.this;
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }

}
