package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.IntResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

public class IntHandler extends ExceptionHandler<IntResolver> implements IntResolver {

    @Override
    public int resolve(Throwable exception) {
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

        public IntHandler mapToInt(ToIntFunction<? super X> exceptionMapper) {
            registerResolver(new IntResolver() {
                @Override
                public boolean isSupported(Throwable exception) {
                    return Configurer.this.isConfiguredFor(exception);
                }

                @Override
                @SuppressWarnings("unchecked")
                public int resolve(Throwable exception) {
                    return exceptionMapper.applyAsInt(intermediateAction.apply((X) exception));
                }
            });
            return IntHandler.this;
        }

        public IntHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToInt(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        public IntHandler supplyDefault(IntSupplier defaultSupplier) {
            return mapToInt(ex -> defaultSupplier.getAsInt());
        }

        public IntHandler returnDefault(int defaultValue) {
            return supplyDefault(() -> defaultValue);
        }

        public IntHandler discard() {
            return returnDefault(0);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }

}
