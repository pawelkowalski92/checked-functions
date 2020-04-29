package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleHandlerTest {

    @SuppressWarnings("unused")
    private static Stream<Arguments> getMultipleExceptions() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("IAE"), 1.0d),
                Arguments.of(new ClassCastException("CCE"), 1.1d),
                Arguments.of(new NoSuchMethodException("NSME"), 1.2d)
        );
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsRethrownAsUnchecked() {
        //given
        IOException exception = new IOException("Checked io exception");

        //when
        DoubleHandler handler = new DoubleHandler()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //then
        assertThrows(UncheckedIOException.class, () -> handler.resolve(exception));
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenReturnsValue() {
        //given
        double defaultValue = 10.5d;
        InterruptedException exception = new InterruptedException();

        //when
        double returnedValue = new DoubleHandler()
                .inCaseOf(InterruptedException.class).returnDouble(defaultValue)
                .resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //when
        DoubleHandler handler = new DoubleHandler()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //then
        assertDoesNotThrow(() -> {
            double emptyValue = handler.resolve(exception);
            assertEquals(Double.NaN, emptyValue);
        });
    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, double defaultValue) {
        //given
        DoubleHandler handler = new DoubleHandler()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class).returnDouble(defaultValue);

        //when
        double returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //when
        DoubleHandler handler = new DoubleHandler();

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, () -> handler.resolve(exception));
    }

}
