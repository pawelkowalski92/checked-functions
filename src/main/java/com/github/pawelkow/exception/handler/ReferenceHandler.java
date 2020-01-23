package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.ReferenceResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReferenceHandler<R> extends ExceptionHandler<ReferenceResolver<R>> implements ReferenceResolver<R> {

    @Override
    public R resolve(Throwable exception) {
        return getResolver(exception).resolve(exception);
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

        public ReferenceHandler<R> rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToValue(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        public ReferenceHandler<R> supplyDefault(Supplier<? extends R> defaultSupplier) {
            return mapToValue(ex -> defaultSupplier.get());
        }

        public ReferenceHandler<R> returnDefault(R defaultValue) {
            return supplyDefault(() -> defaultValue);
        }

        public ReferenceHandler<R> discard() {
            return returnDefault(null);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }

}
