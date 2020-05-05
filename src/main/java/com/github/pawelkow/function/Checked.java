package com.github.pawelkow.function;

import com.github.pawelkow.exception.handler.*;
import com.github.pawelkow.exception.resolver.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.*;

/**
 * Common base for built-in {@link FunctionalInterface functional interfaces} that extends default capabilities by including support for checked exceptions.
 *
 * @param <F> {@link FunctionalInterface functional interface} to be enhanced
 * @param <X> {@link Exception exception} type that is supported
 * @author pawelkowalski92
 */
public interface Checked<F, X extends Exception> {

    /**
     * Default behavior for consuming encountered exceptions:
     *  <ul>
     *     <li>if exception type is subtype of {@link RuntimeException}, rethrow it as is</li>
     *     <li>if exception type is subtype of {@link IOException}, rethrow it as {@link UncheckedIOException}</li>
     *     <li>otherwise rethrow it as {@link UnhandledCheckedException}</li>
     *  </ul>
     * NOTE: if exception type is subtype of {@link InterruptedException}, interruption status will be setup on calling thread
     */
    Consumer<Exception> RETHROW_UNCHECKED = ex -> {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof IOException) {
            throw new UncheckedIOException((IOException) ex);
        }
        if (ex instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        throw new UnhandledCheckedException(ex);
    };

    /**
     * Apply <strong>consuming</strong> strategy against possible exceptions.
     *
     * @param exceptionHandler {@link Consumer} that handles the exception
     * @return enhanced {@link F interface}
     */
    F handleException(Consumer<? super X> exceptionHandler);

    /**
     * Apply <strong>rethrowing</strong> strategy against possible exceptions.
     *
     * @param exceptionMapper {@link Function} that transforms exception into an {@link RuntimeException unchecked} one
     * @return enhanced {@link F interface}
     */
    F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper);

    /**
     * Apply <strong>discarding</strong> strategy against possible exceptions.
     *
     * @return enhanced {@link F interface}
     */
    F discardException();

    /**
     * Extension for {@link Checked enhanced interface} that supports returning an object reference as fallback value.
     *
     * @param <R> reference to be returned
     * @param <F> {@link FunctionalInterface functional interface} to be enhanced
     * @param <X> {@link Exception exception} type that is supported
     */
    interface WithValue<R, F, X extends Exception> extends Checked<F, X> {

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link ReferenceResolver} that simply consumes the exception, equivalent to:
         * <pre>
         *  handleException(new ReferenceHandler&lt;R&gt;()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .handle(exceptionHandler)
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOf().handle(exceptionHandler).discard());
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link ReferenceResolver} that simply rethrows the exception, equivalent to:
         * <pre>
         *  handleException(new ReferenceHandler&lt;R&gt;()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .rethrow(exceptionMapper)
         *  );
         * </pre>
         */
        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOf().rethrow(exceptionMapper));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link ReferenceResolver} that simply ignores the exception, equivalent to:
         * <pre>
         *  handleException(new ReferenceHandler&lt;R&gt;()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F discardException() {
            return handleException(new ReferenceHandler<R>().<X>inCaseOf().discard());
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining a {@link ReferenceResolver} that simply supplies fallback value, equivalent to:
         * <pre>
         *  handleException(new ReferenceHandler&lt;R&gt;()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .supplyDefault(fallbackValueSupplier)
         *  );
         * </pre>
         *
         * @param fallbackValueSupplier {@link Supplier} providing value to be returned
         * @return enhanced {@link F interface}
         */
        default F supplyFallback(Supplier<? extends R> fallbackValueSupplier) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOf().supplyValue(fallbackValueSupplier));
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining a {@link ReferenceResolver} that simply provides fallback value, equivalent to:
         * <pre>
         *  handleException(new ReferenceHandler&lt;R&gt;()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .returnDefault(fallbackValue)
         *  );
         * </pre>
         *
         * @param fallbackValue value to be returned
         * @return enhanced {@link F interface}
         */
        default F returnFallback(R fallbackValue) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOf().returnValue(fallbackValue));
        }

        /**
         * Determine and apply proper strategy against possible exceptions based on suitable resolver.
         *
         * @param resolver {@link ReferenceResolver exception resolver} capable of providing object references as fallback values
         * @return enhanced {@link F interface}
         */
        F handleException(ReferenceResolver<? extends R> resolver);

    }

    /**
     * Extension for {@link Checked enhanced interface} that supports returning an int primitive as fallback value.
     *
     * @param <F> {@link FunctionalInterface functional interface} to be enhanced
     * @param <X> {@link Exception exception} type that is supported
     */
    interface WithInt<F, X extends Exception> extends Checked<F, X> {

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link IntResolver} that simply consumes the exception, equivalent to:
         * <pre>
         *  handleException(new IntHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .handle(exceptionHandler)
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new IntHandler().<X>inCaseOf().handle(exceptionHandler).discard());
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link IntResolver} that simply rethrows the exception, equivalent to:
         * <pre>
         *  handleException(new IntHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .rethrow(exceptionMapper)
         *  );
         * </pre>
         */
        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new IntHandler().<X>inCaseOf().rethrow(exceptionMapper));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link IntResolver} that simply ignores the exception, equivalent to:
         * <pre>
         *  handleException(new IntHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F discardException() {
            return handleException(new IntHandler().<X>inCaseOf().discard());
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining an {@link IntResolver} that simply supplies fallback value, equivalent to:
         * <pre>
         *  handleException(new IntHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .supplyDefault(defaultValueSupplier)
         *  );
         * </pre>
         *
         * @param fallbackValueSupplier {@link IntSupplier} providing value to be returned
         * @return enhanced {@link F interface}
         */
        default F supplyFallback(IntSupplier fallbackValueSupplier) {
            return handleException(new IntHandler().<X>inCaseOf().supplyInt(fallbackValueSupplier));
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining a {@link IntResolver} that simply provides fallback value, equivalent to:
         * <pre>
         *  handleException(new IntHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .returnDefault(fallbackValue)
         *  );
         * </pre>
         *
         * @param fallbackValue value to be returned
         * @return enhanced {@link F interface}
         */
        default F returnFallback(int fallbackValue) {
            return handleException(new IntHandler().<X>inCaseOf().returnInt(fallbackValue));
        }

        /**
         * Determine and apply proper strategy against possible exceptions based on suitable resolver.
         *
         * @param resolver {@link IntResolver exception resolver} capable of providing int primitives as fallback values
         * @return enhanced {@link F interface}
         */
        F handleException(IntResolver resolver);

    }

    /**
     * Extension for {@link Checked enhanced interface} that supports returning a double primitive as fallback value.
     *
     * @param <F> {@link FunctionalInterface functional interface} to be enhanced
     * @param <X> {@link Exception exception} type that is supported
     */
    interface WithDouble<F, X extends Exception> extends Checked<F, X> {

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link DoubleResolver} that simply consumes the exception, equivalent to:
         * <pre>
         *  handleException(new DoubleHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .handle(exceptionHandler)
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new DoubleHandler().<X>inCaseOf().handle(exceptionHandler).discard());
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link DoubleResolver} that simply rethrows the exception, equivalent to:
         * <pre>
         *  handleException(new DoubleHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .rethrow(exceptionMapper)
         *  );
         * </pre>
         */
        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new DoubleHandler().<X>inCaseOf().rethrow(exceptionMapper));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link DoubleResolver} that simply ignores the exception, equivalent to:
         * <pre>
         *  handleException(new DoubleHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F discardException() {
            return handleException(new DoubleHandler().<X>inCaseOf().discard());
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining an {@link DoubleResolver} that simply supplies fallback value, equivalent to:
         * <pre>
         *  handleException(new DoubleHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .supplyDefault(fallbackValueSupplier)
         *  );
         * </pre>
         *
         * @param fallbackValueSupplier {@link DoubleSupplier} providing value to be returned
         * @return enhanced {@link F interface}
         */
        default F supplyFallback(DoubleSupplier fallbackValueSupplier) {
            return handleException(new DoubleHandler().<X>inCaseOf().supplyDouble(fallbackValueSupplier));
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining a {@link DoubleResolver} that simply provides fallback value, equivalent to:
         * <pre>
         *  handleException(new DoubleHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .returnDefault(fallback)
         *  );
         * </pre>
         *
         * @param fallback value to be returned
         * @return enhanced {@link F interface}
         */
        default F returnFallback(double fallback) {
            return handleException(new DoubleHandler().<X>inCaseOf().returnDouble(fallback));
        }

        /**
         * Determine and apply proper strategy against possible exceptions based on suitable resolver.
         *
         * @param resolver {@link DoubleResolver exception resolver} capable of providing double primitives as fallback values
         * @return enhanced {@link F interface}
         */
        F handleException(DoubleResolver resolver);

    }

    /**
     * Extension for {@link Checked enhanced interface} that supports returning a long primitive as fallback value.
     *
     * @param <F> {@link FunctionalInterface functional interface} to be enhanced
     * @param <X> {@link Exception exception} type that is supported
     */
    interface WithLong<F, X extends Exception> extends Checked<F, X> {

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link LongResolver} that simply consumes the exception, equivalent to:
         * <pre>
         *  handleException(new LongHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .handle(exceptionHandler)
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new LongHandler().<X>inCaseOf().handle(exceptionHandler).discard());
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link LongResolver} that simply rethrows the exception, equivalent to:
         * <pre>
         *  handleException(new LongHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .rethrow(exceptionMapper)
         *  );
         * </pre>
         */
        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new LongHandler().<X>inCaseOf().rethrow(exceptionMapper));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link LongResolver} that simply ignores the exception, equivalent to:
         * <pre>
         *  handleException(new LongHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F discardException() {
            return handleException(new LongHandler().<X>inCaseOf().discard());
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining an {@link LongResolver} that simply supplies fallback value, equivalent to:
         * <pre>
         *  handleException(new LongHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .supplyDefault(defaultValueSupplier)
         *  );
         * </pre>
         *
         * @param defaultValueSupplier {@link LongSupplier} providing value to be returned
         * @return enhanced {@link F interface}
         */
        default F supplyLong(LongSupplier defaultValueSupplier) {
            return handleException(new LongHandler().<X>inCaseOf().supplyLong(defaultValueSupplier));
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining a {@link LongResolver} that simply provides fallback value, equivalent to:
         * <pre>
         *  handleException(new LongHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .returnDefault(defaultValue)
         *  );
         * </pre>
         *
         * @param defaultValue value to be returned
         * @return enhanced {@link F interface}
         */
        default F returnLong(long defaultValue) {
            return handleException(new LongHandler().<X>inCaseOf().returnLong(defaultValue));
        }

        /**
         * Determine and apply proper strategy against possible exceptions based on suitable resolver.
         *
         * @param resolver {@link LongResolver exception resolver} capable of providing long primitives as fallback values
         * @return enhanced {@link F interface}
         */
        F handleException(LongResolver resolver);

    }

    /**
     * Extension for {@link Checked enhanced interface} that supports returning a boolean primitive as fallback value.
     *
     * @param <F> {@link FunctionalInterface functional interface} to be enhanced
     * @param <X> {@link Exception exception} type that is supported
     */
    interface WithBoolean<F, X extends Exception> extends Checked<F, X> {

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link BooleanResolver} that simply consumes the exception, equivalent to:
         * <pre>
         *  handleException(new BooleanHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .handle(exceptionHandler)
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new BooleanHandler().<X>inCaseOf().handle(exceptionHandler).discard());
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link BooleanResolver} that simply rethrows the exception, equivalent to:
         * <pre>
         *  handleException(new BooleanHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .rethrow(exceptionMapper)
         *  );
         * </pre>
         */
        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new BooleanHandler().<X>inCaseOf().rethrow(exceptionMapper));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link BooleanResolver} that simply ignores the exception, equivalent to:
         * <pre>
         *  handleException(new BooleanHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F discardException() {
            return handleException(new BooleanHandler().<X>inCaseOf().discard());
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining an {@link BooleanResolver} that simply supplies fallback value, equivalent to:
         * <pre>
         *  handleException(new BooleanSupplier()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .supplyDefault(defaultValueSupplier)
         *  );
         * </pre>
         *
         * @param defaultValueSupplier {@link BooleanSupplier} providing value to be returned
         * @return enhanced {@link F interface}
         */
        default F supplyBoolean(BooleanSupplier defaultValueSupplier) {
            return handleException(new BooleanHandler().<X>inCaseOf().supplyBoolean(defaultValueSupplier));
        }

        /**
         * Apply <strong>return default</strong> strategy against possible exceptions.
         * <p>
         * This is a shortcut for defining a {@link BooleanResolver} that simply provides fallback value, equivalent to:
         * <pre>
         *  handleException(new BooleanHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .returnDefault(defaultValue)
         *  );
         * </pre>
         *
         * @param defaultValue value to be returned
         * @return enhanced {@link F interface}
         */
        default F returnBoolean(boolean defaultValue) {
            return handleException(new BooleanHandler().<X>inCaseOf().returnBoolean(defaultValue));
        }

        /**
         * Determine and apply proper strategy against possible exceptions based on suitable resolver.
         *
         * @param resolver {@link BooleanResolver exception resolver} capable of providing boolean primitives as fallback values
         * @return enhanced {@link F interface}
         */
        F handleException(BooleanResolver resolver);

    }

    /**
     * Extension for {@link Checked enhanced interface} that supports consuming arguments without returning anything.
     *
     * @param <F> {@link FunctionalInterface functional interface} to be enhanced
     * @param <X> {@link Exception exception} type that is supported
     */
    interface WithNoValue<F, X extends Exception> extends Checked<F, X> {

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining a {@link VoidResolver} that simply consumes the exception, equivalent to:
         * <pre>
         *  handleException(new VoidHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .handle(exceptionHandler)
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new VoidHandler().<X>inCaseOf().handle(exceptionHandler).discard());
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link VoidResolver} that simply rethrows the exception, equivalent to:
         * <pre>
         *  handleException(new VoidHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .rethrow(exceptionMapper)
         *  );
         * </pre>
         */
        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new VoidHandler().<X>inCaseOf().rethrow(exceptionMapper));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This is a shortcut for defining an {@link VoidResolver} that simply ignores the exception, equivalent to:
         * <pre>
         *  handleException(new VoidHandler()
         *      .&lt;X&gt;inCaseOfGeneric()
         *      .discard()
         *  );
         * </pre>
         */
        @Override
        default F discardException() {
            return handleException(new VoidHandler().<X>inCaseOf().discard());
        }

        /**
         * Determine and apply proper strategy against possible exceptions based on suitable resolver.
         *
         * @param resolver {@link VoidResolver exception resolver} capable of consuming the exception without returning anything
         * @return enhanced {@link F interface}
         */
        F handleException(VoidResolver resolver);

    }

}
