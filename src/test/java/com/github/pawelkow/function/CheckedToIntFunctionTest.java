package com.github.pawelkow.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.pawelkow.function.CheckedToIntFunction.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckedToIntFunctionTest {

    private int mapDangerously(Object obj) throws IOException {
        if (obj instanceof String) {
            throw new IOException("String");
        }
        if (obj instanceof Integer) {
            throw new IllegalStateException("Integer");
        }
        return String.valueOf(obj).length();
    }

    @Test
    public void givenDefaultFunctionWhenHandlingCheckedExceptionThenItsMappedAndRethrown() {
        //given
        CheckedToIntFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        IntStream arguments = Stream.of("test")
                .mapToInt(mapper);

        //then
        assertThrows(UnhandledCheckedException.class, arguments::findFirst);
    }

    @Test
    public void givenDefaultFunctionWhenHandlingUncheckedExceptionThenItsRethrown() {
        //given
        CheckedToIntFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        IntStream arguments = Stream.of(1)
                .mapToInt(mapper);

        //then
        assertThrows(IllegalStateException.class, arguments::findFirst);
    }

    @Test
    public void givenDefaultFunctionWhenHandlingNoExceptionThenItMaps() {
        //given
        CheckedToIntFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        OptionalInt someInt = Stream.of(Boolean.TRUE)
                .mapToInt(mapper)
                .findFirst();

        //then
        assertEquals(4, someInt.getAsInt());
    }

    @Test
    public void verifyMethodChainUsability() {
        assertEquals(5, Stream.of("a", "b", "c", "d", "e")
                .mapToInt(wrap(this::mapDangerously).returnInt(1))
                .reduce(0, Integer::sum));
    }

}
