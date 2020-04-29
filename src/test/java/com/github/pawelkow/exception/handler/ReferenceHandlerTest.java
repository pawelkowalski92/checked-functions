package com.github.pawelkow.exception.handler;

import org.junit.jupiter.api.Test;
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

        //when
        ReferenceHandler<?> handler = new ReferenceHandler<>()
                .inCaseOf(IOException.class).rethrow(UncheckedIOException::new);

        //then
        assertThrows(UncheckedIOException.class, () -> handler.resolve(exception));
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenReturnsValue() {
        //given
        Object defaultReference = new Object();
        InterruptedException exception = new InterruptedException();

        //when
        Object returnedValue = new ReferenceHandler<>()
                .inCaseOf(InterruptedException.class).returnValue(defaultReference)
                .resolve(exception);

        //then
        assertEquals(defaultReference, returnedValue);
    }

    @Test
    public void givenCheckedExceptionWhenResolvingThenItsDiscarded() {
        //given
        ClassNotFoundException exception = new ClassNotFoundException("Checked exception");

        //when
        ReferenceHandler<?> handler = new ReferenceHandler<>()
                .inCaseOf(ReflectiveOperationException.class).discard();

        //then
        assertDoesNotThrow(() -> {
            Object emptyValue = handler.resolve(exception);
            assertNull(emptyValue);
        });
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

        //when
        ReferenceHandler<?> handler = new ReferenceHandler<>();

        //then
        assertThrows(ExceptionHandlerMisconfigurationException.class, () -> handler.resolve(exception));
    }

    private enum ExceptionAbbreviation {
        IAE,
        CCE,
        NSME
    }

}
