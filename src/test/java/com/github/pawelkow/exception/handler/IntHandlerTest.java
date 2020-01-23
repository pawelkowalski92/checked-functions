package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class IntHandlerTest {

    @SuppressWarnings("unused")
    private static Stream<Arguments> getMultipleExceptions() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("IAE"), 1),
                Arguments.of(new ClassCastException("CCE"), 2),
                Arguments.of(new NoSuchMethodException("NSME"), 3)
        );
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsRethrownAsUnchecked() {
        //given
        IOException exception = new IOException("Checked io exception");

        //when
        IntHandler handler = new IntHandler()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //then
        assertThrows(UncheckedIOException.class, () -> handler.resolve(exception));
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenReturnsValue() {
        //given
        int defaultValue = -5;
        InterruptedException exception = new InterruptedException();

        //when
        int returnedValue = new IntHandler()
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
        IntHandler handler = new IntHandler()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //then
        assertDoesNotThrow(() -> {
            int emptyValue = handler.resolve(exception);
            assertEquals(0, emptyValue);
        });
    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, int defaultValue) {
        //given
        IntHandler handler = new IntHandler()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class).returnDefault(defaultValue);

        //when
        int returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //when
        IntHandler handler = new IntHandler();

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, () -> handler.resolve(exception));
    }

}
