package com.github.pawelkow.exception.handler;

import com.github.pawelkow.exception.resolver.LongResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;

public class LongHandler extends ExceptionHandler<LongResolver> implements LongResolver {

    @Override
    public long resolve(Throwable exception) {
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

        public LongHandler rethrow(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return mapToLong(ex -> {
                throw exceptionMapper.apply(ex);
            });
        }

        public LongHandler supplyDefault(LongSupplier defaultSupplier) {
            return mapToLong(ex -> defaultSupplier.getAsLong());
        }

        public LongHandler returnDefault(long defaultValue) {
            return supplyDefault(() -> defaultValue);
        }

        public LongHandler discard() {
            return returnDefault(0L);
        }

        @Override
        protected Configurer<X> configurer() {
            return this;
        }

    }

}
