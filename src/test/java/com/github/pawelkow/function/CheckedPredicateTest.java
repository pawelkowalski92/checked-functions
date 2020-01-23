package com.github.pawelkow.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.pawelkow.function.CheckedPredicate.wrap;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckedPredicateTest {

    private boolean testDangerously(Object obj) throws IOException {
        if (obj instanceof String) {
            throw new IOException("String");
        }
        if (obj instanceof Integer) {
            throw new IllegalStateException("Integer");
        }
        return obj != null;
    }

    @Test
    public void givenDefaultPredicateWhenHandlingCheckedExceptionThenItsMappedAndRethrown() {
        //given
        CheckedPredicate<Object, IOException> predicate = this::testDangerously;

        //when
        Optional<Object> argument = Optional.of("test");

        //then
        assertThrows(UnhandledCheckedException.class, () -> argument.filter(predicate));
    }

    @Test
    public void givenDefaultPredicateWhenHandlingUncheckedExceptionThenItsRethrown() {
        //given
        CheckedPredicate<Object, IOException> predicate = this::testDangerously;

        //when
        Optional<Object> argument = Optional.of(1);

        //then
        assertThrows(IllegalStateException.class, () -> argument.filter(predicate));
    }

    @Test
    public void givenDefaultPredicateWhenHandlingNoExceptionThenItFilters() {
        //given
        CheckedPredicate<Object, IOException> predicate = this::testDangerously;

        //when
        Optional<Boolean> argument = Optional.of(Boolean.TRUE)
                .filter(predicate);

        //then
        assertTrue(argument.isPresent());
    }

    @Test
    public void verifyMethodChainUsability() {
        assertTrue(Stream.of("a", "b", "c", "d", "e")
                .anyMatch(wrap(this::testDangerously).returnBoolean(true)));
    }

}
