package com.github.pawelkow.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.pawelkow.function.CheckedFunction.wrap;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckedFunctionTest {

    private String mapDangerously(Object obj) throws IOException {
        if (obj instanceof String) {
            throw new IOException("String");
        }
        if (obj instanceof Integer) {
            throw new IllegalStateException("Integer");
        }
        return String.valueOf(obj);
    }

    @Test
    public void givenDefaultFunctionWhenHandlingCheckedExceptionThenItsMappedAndRethrown() {
        //given
        CheckedFunction<Object, String, IOException> mapper = this::mapDangerously;

        //when
        Optional<Object> argument = Optional.of("test");

        //then
        assertThrows(UnhandledCheckedException.class, () -> argument.map(mapper));
    }

    @Test
    public void givenDefaultFunctionWhenHandlingUncheckedExceptionThenItsRethrown() {
        //given
        CheckedFunction<Object, String, IOException> mapper = this::mapDangerously;

        //when
        Optional<Object> argument = Optional.of(1);

        //then
        assertThrows(IllegalStateException.class, () -> argument.map(mapper));
    }

    @Test
    public void givenDefaultFunctionWhenHandlingNoExceptionThenItMaps() {
        //given
        CheckedFunction<Object, String, IOException> mapper = this::mapDangerously;

        //when
        Optional<String> someString = Optional.of(Boolean.TRUE)
                .map(mapper);

        //then
        assertEquals("true", someString.get());
    }

    @Test
    public void verifyMethodChainUsability() {
        assertEquals("xxxxx", Stream.of("a", "b", "c", "d", "e")
                .map(wrap(this::mapDangerously).returnValue("x"))
                .collect(joining()));
    }

}
