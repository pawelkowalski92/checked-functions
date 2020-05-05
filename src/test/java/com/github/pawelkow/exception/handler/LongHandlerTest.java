package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongHandlerTest {

    @SuppressWarnings("unused")
    private static Stream<Arguments> getMultipleExceptions() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("IAE"), 1L),
                Arguments.of(new ClassCastException("CCE"), 2L),
                Arguments.of(new NoSuchMethodException("NSME"), 3L)
        );
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsRethrownAsUnchecked() {
        //given
        IOException exception = new IOException("Checked io exception");

        //and
        LongHandler handler = new LongHandler()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(UncheckedIOException.class, resolve);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenReturnsValue() {
        //given
        long defaultValue = -5L;
        InterruptedException exception = new InterruptedException();

        //and
        LongHandler handler = new LongHandler()
                .inCaseOf(InterruptedException.class).returnLong(defaultValue);

        //when
        long returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //and
        LongHandler handler = new LongHandler()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //when
        long emptyValue = handler.resolve(exception);

        //then
        assertEquals(0L, emptyValue);
    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, long defaultValue) {
        //given
        LongHandler handler = new LongHandler()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class).returnLong(defaultValue);

        //when
        long returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //and
        LongHandler handler = new LongHandler();

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, resolve);
    }

}
