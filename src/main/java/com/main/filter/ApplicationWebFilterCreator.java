package com.main.filter;

import com.main.util.AnnotationUtils;
import filter.AbstractFilterCreator;
import filter.Filters;
import filter.annotation.WebFilter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ApplicationWebFilterCreator implements AbstractFilterCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final WebFilterComponentFilterCreator webFilterComponentFilterCreator;
    private final List<Class<?>> clazzes;

    public ApplicationWebFilterCreator(WebFilterComponentFilterCreator webFilterComponentFilterCreator, List<Class<?>> clazzes) {
        Objects.requireNonNull(webFilterComponentFilterCreator, "container is null.");
        Objects.requireNonNull(clazzes, "container is null.");

        this.clazzes = clazzes.stream()
                .filter(clazz -> !Objects.isNull(clazz))
                .filter(clazz -> AnnotationUtils.exist(clazz, WEB_FILTER_CLASS))
                .collect(Collectors.toUnmodifiableList());
        this.webFilterComponentFilterCreator = webFilterComponentFilterCreator;
    }

    public Filters create() {
        return clazzes.stream()
                .map(webFilterComponentFilterCreator::create)
                .reduce(Filters.empty(), Filters::merge);
    }
}