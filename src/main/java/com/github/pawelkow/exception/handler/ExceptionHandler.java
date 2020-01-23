package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.ExceptionResolver;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class ExceptionHandler<R extends ExceptionResolver> implements ExceptionResolver {

    private final List<R> registeredResolvers = new LinkedList<>();

    void registerResolver(R resolver) {
        registeredResolvers.add(resolver);
    }

    Optional<R> getSuitableResolver(Throwable exception) {
        return registeredResolvers.stream()
                .filter(ExceptionResolver.supports(exception))
                .findFirst();
    }

    R getResolver(Throwable exception) {
        return getSuitableResolver(exception).orElseThrow(() -> new ExceptionHandlerMisconfigurationException(exception));
    }

    @Override
    public boolean isSupported(Throwable exception) {
        return getSuitableResolver(exception).isPresent();
    }

    abstract static class Configurer<X extends Throwable, C extends Configurer<X, C>> {

        private final Set<Class<? extends X>> resolvedTypes = new HashSet<>();
        Function<X, X> intermediateAction = Function.identity();

        C forType(Class<? extends X> resolvedType) {
            this.resolvedTypes.add(resolvedType);
            return configurer();
        }

        C forTypes(Collection<Class<? extends X>> resolvedTypes) {
            this.resolvedTypes.addAll(resolvedTypes);
            return configurer();
        }

        public C handle(Consumer<? super X> handler) {
            intermediateAction = intermediateAction.andThen(x -> {
                handler.accept(x);
                return x;
            });
            return configurer();
        }

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

        protected abstract C configurer();

    }

}