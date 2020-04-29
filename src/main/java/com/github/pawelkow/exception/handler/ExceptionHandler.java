package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.ExceptionResolver;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Common base for variation of exception handlers consisting of multiple resolvers, allowing for resolution of more than one kind of exception by the same handler.
 *
 * @param <R> the type of supported {@link ExceptionResolver exception resolvers}
 */
abstract class ExceptionHandler<R extends ExceptionResolver> implements ExceptionResolver {

    private final List<R> registeredResolvers = new LinkedList<>();

    void registerResolver(R resolver) {
        registeredResolvers.add(resolver);
    }

    /**
     * Filter registered resolvers and get the first one that supports the exception's type (by order of insertion).
     *
     * @param exception exception to be resolved
     * @return {@link Optional} containing proper resolver or {@link Optional#empty()} if none found
     */
    Optional<R> getSuitableResolver(Throwable exception) {
        return registeredResolvers.stream()
                .filter(ExceptionResolver.supports(exception))
                .findFirst();
    }

    /**
     * Filter registered resolvers and get the first one that supports the exception's type (by order of insertion).
     *
     * @param exception exception to be resolved
     * @return {@link ExceptionResolver} suitable resolver
     * @throws ExceptionHandlerMisconfigurationException if there's no resolver registered that supports resolved exception's type
     */
    R getResolver(Throwable exception) {
        return getSuitableResolver(exception).orElseThrow(() -> new ExceptionHandlerMisconfigurationException(exception));
    }

    @Override
    public boolean isSupported(Throwable exception) {
        return getSuitableResolver(exception).isPresent();
    }

    /**
     * Common base for {@link ExceptionHandler handler} configurators that provide basic exception related operations.
     *
     * @param <X> the exception type that serves as supertype for all exceptions that should be covered
     * @param <C> the type of configurer
     */
    abstract static class Configurer<X extends Throwable, C extends Configurer<X, C>> {

        private final Set<Class<? extends X>> resolvedTypes = new HashSet<>();
        Function<X, X> intermediateAction = Function.identity();

        /**
         * Configure the handler for particular type of exception.
         *
         * @param resolvedType the type of exception to be resolved
         * @return this configurer
         */
        C forType(Class<? extends X> resolvedType) {
            this.resolvedTypes.add(resolvedType);
            return configurer();
        }

        /**
         * Configure the handler for several types of exceptions.
         *
         * @param resolvedTypes the types of exceptions to be resolved
         * @return this configurer
         */
        C forTypes(Collection<Class<? extends X>> resolvedTypes) {
            this.resolvedTypes.addAll(resolvedTypes);
            return configurer();
        }

        /**
         * Add intermediate operation to be performed during resolution of exception, e.g. logging.
         *
         * @param handler {@link Consumer} representing simple operation
         * @return this configurer
         */
        public C handle(Consumer<? super X> handler) {
            intermediateAction = intermediateAction.andThen(x -> {
                handler.accept(x);
                return x;
            });
            return configurer();
        }

        /**
         * Verify if this configurer supports resolution of provided exception.
         *
         * @param exception {@link Throwable} to be resolved
         * @return {@code true} if this exception may be resolved, otherwise {@code false}
         */
        @SuppressWarnings("unchecked")
        boolean isConfiguredFor(Throwable exception) {
            if (resolvedTypes.isEmpty()) {
                try {
                    X castedException = (X) exception;
                    return castedException != null;
                } catch (ClassCastException ex) {
                    return false;
                }
            }
            return resolvedTypes.stream().anyMatch(type -> type.isInstance(exception));
        }

        /**
         * Gets this configurer.
         * <p>
         * NOTE: should be overridden in concrete class to support method chaining properly.
         *
         * @return this configurer
         */
        protected abstract C configurer();

    }

}