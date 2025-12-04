package fpl.excel.builder;

import java.util.function.Function;

public record Col<T>(
        String title,
        Function<T, Object> extractor
) {}
