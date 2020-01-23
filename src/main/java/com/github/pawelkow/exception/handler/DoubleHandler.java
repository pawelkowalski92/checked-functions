package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.DoubleResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class DoubleHandler extends ExceptionHandler<DoubleResolver> implements DoubleResolver {

    @Override
    public double resolve(Throwable exception) {
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

        public DoubleHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToDouble(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        public DoubleHandler supplyDefault(DoubleSupplier defaultSupplier) {
            return mapToDouble(ex -> defaultSupplier.getAsDouble());
        }

        public DoubleHandler returnDefault(double defaultValue) {
            return supplyDefault(() -> defaultValue);
        }

        public DoubleHandler discard() {
            return returnDefault(Double.NaN);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }
}
