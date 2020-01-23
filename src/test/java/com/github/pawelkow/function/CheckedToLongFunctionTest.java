package com.github.pawelkow.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.OptionalLong;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.github.pawelkow.function.CheckedToLongFunction.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckedToLongFunctionTest {

    private long mapDangerously(Object obj) throws IOException {
        if (obj instanceof String) {
            throw new IOException("String");
        }
        if (obj instanceof Integer) {
            throw new IllegalStateException("Longeger");
        }
        return String.valueOf(obj).length();
    }

    @Test
    public void givenDefaultFunctionWhenHandlingCheckedExceptionThenItsMappedAndRethrown() {
        //given
        CheckedToLongFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        LongStream arguments = Stream.of("test")
                .mapToLong(mapper);

        //then
        assertThrows(UnhandledCheckedException.class, arguments::findFirst);
    }

    @Test
    public void givenDefaultFunctionWhenHandlingUncheckedExceptionThenItsRethrown() {
        //given
        CheckedToLongFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        LongStream arguments = Stream.of(1)
                .mapToLong(mapper);

        //then
        assertThrows(IllegalStateException.class, arguments::findFirst);
    }

    @Test
    public void givenDefaultFunctionWhenHandlingNoExceptionThenItMaps() {
        //given
        CheckedToLongFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        OptionalLong someLong = Stream.of(Boolean.TRUE)
                .mapToLong(mapper)
                .findFirst();

        //then
        assertEquals(4, someLong.getAsLong());
    }

    @Test
    public void verifyMethodChainUsability() {
        assertEquals(5, Stream.of("a", "b", "c", "d", "e")
                .mapToLong(wrap(this::mapDangerously).returnLong(1))
                .reduce(0, Long::sum));
    }

}
