package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.BooleanResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

public class BooleanHandler extends ExceptionHandler<BooleanResolver> implements BooleanResolver {

    @Override
    public boolean resolve(Throwable exception) {
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

        public BooleanHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToBoolean(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        public BooleanHandler supplyDefault(BooleanSupplier defaultSupplier) {
            return mapToBoolean(ex -> defaultSupplier.getAsBoolean());
        }

        public BooleanHandler returnDefault(boolean defaultValue) {
            return supplyDefault(() -> defaultValue);
        }

        public BooleanHandler discard() {
            return returnDefault(false);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }
}
