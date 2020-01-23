package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.VoidResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class VoidHandler extends ExceptionHandler<VoidResolver> implements VoidResolver {

    @Override
    public void resolve(Throwable exception) {
        getResolver(exception).resolve(exception);
    }

    public <X extends Throwable> Configurer<X> inCaseOfGeneric() {
        return new Configurer<>();
    }

    public <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X> exceptionType) {
        return new Configurer<X>().forType(exceptionType);
    }

    public Configurer<Throwable> ifNoneMatch() {
        return inCaseOf(Throwable.class);
    }

    public <X extends Throwable> Configurer<X> inCaseOf(Collection<Class<? extends X>> exceptionTypes) {
        return new Configurer<X>().forTypes(exceptionTypes);
    }

    @SafeVarargs
    public final <X extends Throwable> Configurer<X> inCaseOf(Class<? extends X>... exceptionTypes) {
        return inCaseOf(Arrays.asList(exceptionTypes));
    }

    public class Configurer<X extends Throwable> extends ExceptionHandler.Configurer<X, Configurer<X>> {

        private Configurer() {
        }

        public VoidHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handle(ex -> {
                throw exceptionMapper.apply(ex);
            }).discard();
        }

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
