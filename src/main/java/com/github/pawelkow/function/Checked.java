package com.github.pawelkow.function;

import com.github.pawelkow.exception.handler.*;
import com.github.pawelkow.exception.resolver.*;

import java.util.function.*;

public interface Checked<F, X extends Exception> {

    Consumer<Exception> RETHROW_UNCHECKED = ex -> {
        throw (ex instanceof RuntimeException) ? (RuntimeException) ex : new UnhandledCheckedException(ex);
    };

    F handleException(Consumer<? super X> exceptionHandler);

    F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper);

    F discardException();

    interface WithValue<R, F, X extends Exception> extends Checked<F, X> {

        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOfGeneric().handle(exceptionHandler).discard());
        }

        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOfGeneric().rethrow(exceptionMapper));
        }

        @Override
        default F discardException() {
            return handleException(new ReferenceHandler<R>().<X>inCaseOfGeneric().discard());
        }

        default F supplyValue(Supplier<? extends R> defaultValueSupplier) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOfGeneric().supplyDefault(defaultValueSupplier));
        }

        default F returnValue(R defaultValue) {
            return handleException(new ReferenceHandler<R>().<X>inCaseOfGeneric().returnDefault(defaultValue));
        }

        F handleException(ReferenceResolver<R> resolver);

    }

    interface WithInt<F, X extends Exception> extends Checked<F, X> {

        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new IntHandler().<X>inCaseOfGeneric().handle(exceptionHandler).discard());
        }

        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new IntHandler().<X>inCaseOfGeneric().rethrow(exceptionMapper));
        }

        @Override
        default F discardException() {
            return handleException(new IntHandler().<X>inCaseOfGeneric().discard());
        }

        default F supplyInt(IntSupplier defaultValueSupplier) {
            return handleException(new IntHandler().<X>inCaseOfGeneric().supplyDefault(defaultValueSupplier));
        }

        default F returnInt(int defaultValue) {
            return handleException(new IntHandler().<X>inCaseOfGeneric().returnDefault(defaultValue));
        }

        F handleException(IntResolver resolver);

    }

    interface WithDouble<F, X extends Exception> extends Checked<F, X> {

        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new DoubleHandler().<X>inCaseOfGeneric().handle(exceptionHandler).discard());
        }

        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new DoubleHandler().<X>inCaseOfGeneric().rethrow(exceptionMapper));
        }

        @Override
        default F discardException() {
            return handleException(new DoubleHandler().<X>inCaseOfGeneric().discard());
        }

        default F supplyDouble(DoubleSupplier defaultValueSupplier) {
            return handleException(new DoubleHandler().<X>inCaseOfGeneric().supplyDefault(defaultValueSupplier));
        }

        default F returnDouble(double defaultValue) {
            return handleException(new DoubleHandler().<X>inCaseOfGeneric().returnDefault(defaultValue));
        }

        F handleException(DoubleResolver resolver);

    }

    interface WithLong<F, X extends Exception> extends Checked<F, X> {

        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new LongHandler().<X>inCaseOfGeneric().handle(exceptionHandler).discard());
        }

        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new LongHandler().<X>inCaseOfGeneric().rethrow(exceptionMapper));
        }

        @Override
        default F discardException() {
            return handleException(new LongHandler().<X>inCaseOfGeneric().discard());
        }

        default F supplyLong(LongSupplier defaultValueSupplier) {
            return handleException(new LongHandler().<X>inCaseOfGeneric().supplyDefault(defaultValueSupplier));
        }

        default F returnLong(long defaultValue) {
            return handleException(new LongHandler().<X>inCaseOfGeneric().returnDefault(defaultValue));
        }

        F handleException(LongResolver resolver);

    }

    interface WithBoolean<F, X extends Exception> extends Checked<F, X> {

        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new BooleanHandler().<X>inCaseOfGeneric().handle(exceptionHandler).discard());
        }

        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new BooleanHandler().<X>inCaseOfGeneric().rethrow(exceptionMapper));
        }

        @Override
        default F discardException() {
            return handleException(new BooleanHandler().<X>inCaseOfGeneric().discard());
        }

        default F supplyBoolean(BooleanSupplier defaultValueSupplier) {
            return handleException(new BooleanHandler().<X>inCaseOfGeneric().supplyDefault(defaultValueSupplier));
        }

        default F returnBoolean(boolean defaultValue) {
            return handleException(new BooleanHandler().<X>inCaseOfGeneric().returnDefault(defaultValue));
        }

        F handleException(BooleanResolver resolver);

    }

    interface WithNoValue<F, X extends Exception> extends Checked<F, X> {

        @Override
        default F handleException(Consumer<? super X> exceptionHandler) {
            return handleException(new VoidHandler().<X>inCaseOfGeneric().handle(exceptionHandler).discard());
        }

        @Override
        default F rethrowException(Function<? super X, ? extends RuntimeException> exceptionMapper) {
            return handleException(new VoidHandler().<X>inCaseOfGeneric().rethrow(exceptionMapper));
        }

        @Override
        default F discardException() {
            return handleException(new VoidHandler().<X>inCaseOfGeneric().discard());
        }

        F handleException(VoidResolver resolver);

    }

}
