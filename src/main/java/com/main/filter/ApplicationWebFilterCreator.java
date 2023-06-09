package com.main.filter;

import container.Container;
import filter.Filters;
import filter.annotation.WebFilter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import util.AnnotationUtils;

public class ApplicationWebFilterCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final WebFilterComponentFilterCreator webFilterComponentFilterCreator;

    public ApplicationWebFilterCreator(WebFilterComponentFilterCreator webFilterComponentFilterCreator) {
        Objects.requireNonNull(webFilterComponentFilterCreator, "container is null.");

        this.webFilterComponentFilterCreator = webFilterComponentFilterCreator;
    }

    public static ApplicationWebFilterCreator from(Container container) {
        Objects.requireNonNull(container, "container is null.");

        WebFilterComponentFilterCreator filterCreator = new WebFilterComponentFilterCreator(container);
        return new ApplicationWebFilterCreator(filterCreator);
    }

    public Filters create(List<Class<?>> webFilterClazzes) {
        Objects.requireNonNull(webFilterClazzes, "container is null.");

        webFilterClazzes = webFilterClazzes.stream()
            .filter(clazz -> !Objects.isNull(clazz))
            .filter(clazz -> AnnotationUtils.exist(clazz, WEB_FILTER_CLASS))
            .collect(Collectors.toUnmodifiableList());

        return webFilterClazzes.stream()
            .map(webFilterComponentFilterCreator::create)
            .reduce(Filters.empty(), Filters::merge);
    }
}
