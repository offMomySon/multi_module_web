package com.main.filter;

import annotation.Component;
import container.Container;
import filter.AbstractFilterCreator;
import filter.FilterWorker;
import filter.Filters;
import filter.WebFilterAnnotatedFilterCreator;
import filter.annotation.WebFilter;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
import util.AnnotationUtils;

public class WebFilterComponentFilterCreator extends AbstractFilterCreator {
    private static final Class<Component> COMPONENT_CLASS = Component.class;
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final Container container;
    private final Class<?> filterWorkerClazz;

    public WebFilterComponentFilterCreator(Container container, Class<?> filterWorkerClazz) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(filterWorkerClazz);

        if (AnnotationUtils.doesNotExist(filterWorkerClazz, WEB_FILTER_CLASS)) {
            throw new RuntimeException("does not exist component annotation");
        }

        this.container = container;
        this.filterWorkerClazz = filterWorkerClazz;
    }

    @Override
    public Filters create() {
        Class<?>[] memberClasses = AnnotationUtils.peekFieldsType(filterWorkerClazz, COMPONENT_CLASS).toArray(Class<?>[]::new);
        Object[] memberObjects = Arrays.stream(memberClasses).map(container::get).toArray(Object[]::new);
        FilterWorker filterWorker = (FilterWorker) newObject(memberClasses, memberObjects);

        WebFilterAnnotatedFilterCreator filterCreator = new WebFilterAnnotatedFilterCreator(filterWorker);
        return filterCreator.create();
    }
    
    private Object newObject(Class<?>[] memberClasses, Object[] memberObjects) {
        try {
            Constructor<?> constructor = filterWorkerClazz.getConstructor(memberClasses);
            return constructor.newInstance(memberObjects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
