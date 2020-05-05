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

public class ReferenceHandlerTest {

    @SuppressWarnings("unused")
    private static Stream<Arguments> getMultipleExceptions() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("IAE"), ExceptionAbbreviation.IAE),
                Arguments.of(new ClassCastException("CCE"), ExceptionAbbreviation.CCE),
                Arguments.of(new NoSuchMethodException("NSME"), ExceptionAbbreviation.NSME)
        );
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsRethrownAsUnchecked() {
        //given
        IOException exception = new IOException("Checked io exception");

        //and
        ReferenceHandler<?> handler = new ReferenceHandler<>()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(UncheckedIOException.class, resolve);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenReturnsValue() {
        //given
        Object defaultReference = new Object();
        InterruptedException exception = new InterruptedException();

        //and
        ReferenceHandler<?> handler = new ReferenceHandler<>()
                .inCaseOf(InterruptedException.class).returnValue(defaultReference);

        //when
        Object returnedValue = handler.resolve(exception);

        //then
        assertEquals(defaultReference, returnedValue);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //and
        ReferenceHandler<?> handler = new ReferenceHandler<>()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //when
        Object emptyValue = handler.resolve(exception);

        //then
        assertNull(emptyValue);

    }

    @ParameterizedTest
    @MethodSource("getMultipleExceptions")
    public void givenMultipleExceptionsWhenResolvingThenItsConsumed(Exception exception, ExceptionAbbreviation defaultReference) {
        //given
        ReferenceHandler<ExceptionAbbreviation> handler = new ReferenceHandler<ExceptionAbbreviation>()
                .inCaseOf(IllegalArgumentException.class, ClassCastException.class, NoSuchMethodException.class).returnValue(defaultReference);

        //when
        ExceptionAbbreviation returnedReference = handler.resolve(exception);

        //then
        assertEquals(defaultReference, returnedReference);
    }

    @Test
    public void givenExceptionWhenResolvingWithNotConfiguredHandlerThenAnotherIsThrown() {
        //given
        IllegalStateException exception = new IllegalStateException("notConfigured");

        //and
        ReferenceHandler<?> handler = new ReferenceHandler<>();

        //when
        Executable resolve = () -> handler.resolve(exception);

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, resolve);
    }

    private enum ExceptionAbbreviation {
        IAE,
        CCE,
        NSME
    }

}
