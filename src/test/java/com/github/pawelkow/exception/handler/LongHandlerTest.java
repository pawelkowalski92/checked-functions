package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

        //when
        LongHandler handler = new LongHandler()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //then
        assertThrows(UncheckedIOException.class, () -> handler.resolve(exception));
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenReturnsValue() {
        //given
        long defaultValue = -5L;
        InterruptedException exception = new InterruptedException();

        //when
        long returnedValue = new LongHandler()
                .inCaseOf(InterruptedException.class).returnDefault(defaultValue)
                .resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //when
        LongHandler handler = new LongHandler()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //then
        assertDoesNotThrow(() -> {
            long emptyValue = handler.resolve(exception);
            assertEquals(0L, emptyValue);
        });
    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, long defaultValue) {
        //given
        LongHandler handler = new LongHandler()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class).returnDefault(defaultValue);

        //when
        long returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //when
        LongHandler handler = new LongHandler();

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, () -> handler.resolve(exception));
    }

}
