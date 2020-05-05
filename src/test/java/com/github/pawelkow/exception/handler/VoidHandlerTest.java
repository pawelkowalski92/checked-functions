package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
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

        //and
        VoidHandler handler = new VoidHandler()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(UncheckedIOException.class, resolve);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsConsumed() {
        //given
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        InterruptedException exception = new InterruptedException();

        //and
        VoidHandler handler = new VoidHandler()
                .inCaseOf(InterruptedException.class).handle(printer::print).discard();

        //when
        handler.resolve(exception);
        printer.close();

        //then
        assertEquals(exception.toString(), writer.toString());
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //and
        VoidHandler handler = new VoidHandler()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertDoesNotThrow(resolve);
    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, String cause) {
        //given
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);

        //and
        VoidHandler handler = new VoidHandler()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class)
                .handle(ex -> printer.print(ex.getMessage())).discard();

        //when
        handler.resolve(exception);
        printer.close();

        //then
        assertEquals(cause, writer.toString());
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //and
        VoidHandler handler = new VoidHandler();

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, resolve);
    }

}
