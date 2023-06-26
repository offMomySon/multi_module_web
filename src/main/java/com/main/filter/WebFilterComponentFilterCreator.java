package com.main.filter;

import com.main.util.AnnotationUtils;
import container.Container;
import container.annotation.Component;
import filter.FilterWorker;
import filter.Filters;
import filter.WebFilterAnnotatedFilterCreator;
import filter.annotation.WebFilter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

public class WebFilterComponentFilterCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;
    private static final Class<Component> COMPONENT_CLASS = Component.class;

    private final Container container;

    public WebFilterComponentFilterCreator(Container container) {
        Objects.requireNonNull(container);
        this.container = container;
    }

    public Filters create(Class<?> filterWorkerClazz) {
        Objects.requireNonNull(filterWorkerClazz);
        if (AnnotationUtils.doesNotExist(filterWorkerClazz, WEB_FILTER_CLASS)) {
            throw new RuntimeException("does not exist component annotation");
        }

        Class<?>[] memberClasses = AnnotationUtils.peekFieldsType(filterWorkerClazz, COMPONENT_CLASS).toArray(Class<?>[]::new);
        Object[] memberObjects = Arrays.stream(memberClasses).map(container::get).toArray(Object[]::new);
        FilterWorker filterWorker = (FilterWorker) newObject(filterWorkerClazz, memberClasses, memberObjects);

        WebFilterAnnotatedFilterCreator filterCreator = new WebFilterAnnotatedFilterCreator(filterWorker);
        return filterCreator.create();
    }

    private static Object newObject(Class<?> filterWorkerClazz, Class<?>[] memberClasses, Object[] memberObjects) {
        try {
            Constructor<?> constructor = filterWorkerClazz.getConstructor(memberClasses);
            return constructor.newInstance(memberObjects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
