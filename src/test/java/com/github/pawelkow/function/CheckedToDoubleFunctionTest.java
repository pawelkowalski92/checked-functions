package com.github.pawelkow.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static com.github.pawelkow.function.CheckedToDoubleFunction.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckedToDoubleFunctionTest {

    private double mapDangerously(Object obj) throws IOException {
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
        CheckedToDoubleFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        DoubleStream arguments = Stream.of("test")
                .mapToDouble(mapper);

        //then
        assertThrows(UnhandledCheckedException.class, arguments::findFirst);
    }

    @Test
    public void givenDefaultFunctionWhenHandlingUncheckedExceptionThenItsRethrown() {
        //given
        CheckedToDoubleFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        DoubleStream arguments = Stream.of(1)
                .mapToDouble(mapper);

        //then
        assertThrows(IllegalStateException.class, arguments::findFirst);
    }

    @Test
    public void givenDefaultFunctionWhenHandlingNoExceptionThenItMaps() {
        //given
        CheckedToDoubleFunction<Object, IOException> mapper = this::mapDangerously;

        //when
        OptionalDouble someDouble = Stream.of(Boolean.TRUE)
                .mapToDouble(mapper)
                .findFirst();

        //then
        assertEquals(4d, someDouble.getAsDouble());
    }

    @Test
    public void verifyMethodChainUsability() {
        assertEquals(2.5d, Stream.of("a", "b", "c", "d", "e")
                .mapToDouble(wrap(this::mapDangerously).returnDouble(0.5d))
                .reduce(0d, Double::sum));
    }

}
