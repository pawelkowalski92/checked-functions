package com.github.pawelkow.exception.resolver;

import java.util.function.Predicate;

public interface ExceptionResolver {

    static Predicate<ExceptionResolver> supports(Throwable exception) {
        return resolver -> resolver.isSupported(exception);
    }

    boolean isSupported(Throwable exception);

}
