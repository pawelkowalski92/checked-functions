package com.github.pawelkow.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.pawelkow.function.CheckedConsumer.wrap;
import static org.junit.jupiter.api.Assertions.*;

public class CheckedConsumerTest {

    private StringWriter loggingContainer;

    private void consumeDangerously(Object obj) throws IOException {
        if (obj instanceof String) {
            throw new IOException("String");
        }
        if (obj instanceof Integer) {
            throw new IllegalStateException("Integer");
        }
        loggingContainer.write(MessageFormat.format("Consuming object: {0}", obj));
    }

    @BeforeEach
    public void setUp() {
        this.loggingContainer = new StringWriter();
    }

    @Test
    public void givenDefaultConsumerWhenHandlingCheckedExceptionThenItsMappedAndRethrown() {
        //given
        CheckedConsumer<Object, Exception> consumer = this::consumeDangerously;

        //when
        Optional<Object> argument = Optional.of("test");

        //then
        assertThrows(UnhandledCheckedException.class, () -> argument.ifPresent(consumer));
        assertTrue(loggingContainer.toString().isEmpty());
    }

    @Test
    public void givenDefaultConsumerWhenHandlingUncheckedExceptionThenItsRethrown() {
        //given
        CheckedConsumer<Object, Exception> consumer = this::consumeDangerously;

        //when
        Optional<Object> argument = Optional.of(1);

        //then
        assertThrows(IllegalStateException.class, () -> argument.ifPresent(consumer));
        assertTrue(loggingContainer.toString().isEmpty());
    }

    @Test
    public void givenDefaultConsumerWhenHandlingNoExceptionThenItConsumes() {
        //given
        CheckedConsumer<Object, Exception> consumer = this::consumeDangerously;

        //when
        Optional<Object> argument = Optional.of(new Object());

        //then
        assertDoesNotThrow(() -> argument.ifPresent(consumer));
        assertFalse(loggingContainer.toString().isEmpty());
    }

    @Test
    public void verifyMethodChainUsability() {
        assertDoesNotThrow(() -> Stream.of("a", "b", "c", "d", "e")
                .forEach(wrap(this::consumeDangerously).discardException()));
    }

}
