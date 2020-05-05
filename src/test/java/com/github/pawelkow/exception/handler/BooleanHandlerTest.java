package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanHandlerTest {

    @SuppressWarnings("unused")
    private static Stream<Arguments> getMultipleExceptions() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("IAE"), true),
                Arguments.of(new ClassCastException("CCE"), false),
                Arguments.of(new NoSuchMethodException("NSME"), true)
        );
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsRethrownAsUnchecked() {
        //given
        IOException exception = new IOException("Checked io exception");

        //and
        BooleanHandler handler = new BooleanHandler()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(UncheckedIOException.class, resolve);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenReturnsValue() {
        //given
        boolean defaultValue = Character.isUpperCase('A');
        InterruptedException exception = new InterruptedException();

        //and
        BooleanHandler handler = new BooleanHandler()
                .inCaseOf(InterruptedException.class).returnBoolean(defaultValue);

        //when
        boolean returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //and
        BooleanHandler handler = new BooleanHandler()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //when
        boolean emptyValue = handler.resolve(exception);

        //then
        assertFalse(emptyValue);
    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, boolean defaultValue) {
        //given
        BooleanHandler handler = new BooleanHandler()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class).returnBoolean(defaultValue);

        //when
        boolean returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultValue, returnedValue);
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //and
        BooleanHandler handler = new BooleanHandler();

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, resolve);
    }

}
