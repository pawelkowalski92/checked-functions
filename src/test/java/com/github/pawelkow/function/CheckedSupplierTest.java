package com.github.pawelkow.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.pawelkow.function.CheckedSupplier.wrap;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public class CheckedSupplierTest {

    private int randomFate;

    private String supplyDangerously() throws IOException {
        switch (randomFate) {
            case 1:
                throw new IOException();
            case 2:
                throw new IllegalStateException("wrong fate");
            default:
                return "OK";
        }
    }

    @Test
    public void givenDefaultSupplierWhenHandlingCheckedExceptionThenItsMappedAndRethrown() {
        //given
        CheckedSupplier<String, IOException> supplier = this::supplyDangerously;
        randomFate = 1;

        //when
        List<String> strings = new ArrayList<>();

        //then
        assertThrows(UncheckedIOException.class, () -> strings.add(supplier.get()));
        assertTrue(strings.isEmpty());
    }

    @Test
    public void givenDefaultSupplierWhenHandlingUncheckedExceptionThenItsRethrown() {
        //given
        CheckedSupplier<String, IOException> supplier = this::supplyDangerously;
        randomFate = 2;

        //when
        List<String> strings = new ArrayList<>();

        //then
        assertThrows(IllegalStateException.class, () -> strings.add(supplier.get()));
        assertTrue(strings.isEmpty());
    }

    @Test
    public void givenDefaultSupplierWhenHandlingNoExceptionThenItSupplies() {
        //given
        CheckedSupplier<String, IOException> supplier = this::supplyDangerously;
        randomFate = 0;

        //when
        List<String> strings = new ArrayList<>();

        //then
        assertDoesNotThrow(() -> strings.add(supplier.get()));
        assertIterableEquals(singletonList("OK"), strings);
    }

    @Test
    public void verifyMethodChainUsability() {
        assertEquals("OK", Optional.of("ABC")
                .filter(str -> str.chars().allMatch(Character::isLowerCase))
                .orElseGet(wrap(this::supplyDangerously)));
    }

}
