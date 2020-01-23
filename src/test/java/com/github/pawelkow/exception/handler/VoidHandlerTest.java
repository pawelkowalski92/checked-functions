package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class VoidHandlerTest {

    @SuppressWarnings("unused")
    private static Stream<Arguments> getMultipleExceptions() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("IAE"), "IAE"),
                Arguments.of(new ClassCastException("CCE"), "CCE"),
                Arguments.of(new NoSuchMethodException("NSME"), "NSME")
        );
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsRethrownAsUnchecked() {
        //given
        IOException exception = new IOException("Checked io exception");

        //when
        VoidHandler handler = new VoidHandler()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //then
        assertThrows(UncheckedIOException.class, () -> handler.resolve(exception));
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsConsumed() {
        //given
        StringWriter writer = new StringWriter();
        InterruptedException exception = new InterruptedException();

        //when
        try (PrintWriter printer = new PrintWriter(writer)) {
            new VoidHandler()
                    .inCaseOf(InterruptedException.class).handle(printer::print).discard()
                    .resolve(exception);
        }

        //then
        assertEquals(exception.toString(), writer.toString());
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //when
        VoidHandler handler = new VoidHandler()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //then
        assertDoesNotThrow(() -> handler.resolve(exception));
    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, String cause) {
        //given
        StringWriter writer = new StringWriter();
        VoidHandler handler = new VoidHandler()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class).handle(ex -> writer.write(ex.getMessage())).discard();

        //when
        handler.resolve(exception);

        //then
        assertEquals(cause, writer.toString());
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //when
        VoidHandler handler = new VoidHandler();

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, () -> handler.resolve(exception));
    }

}
