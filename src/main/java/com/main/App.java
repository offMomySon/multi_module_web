package com.main;


import com.main.container.ComponentContainerCreator;
import com.main.container.Container;
import com.main.container.resource.ClassFinder;
import com.main.container.resource.ResourceFinder;
import com.main.executor.ApplicationRequestExecutor;
import com.main.executor.MethodExecutor;
import com.main.executor.StaticResourceExecutor;
import com.main.filter.ApplicationWebFilterCreator;
import com.main.matcher.ControllerHttpPathMatcherCreator;
import com.main.matcher.HttpPathMatcher;
import com.main.matcher.converter.base.CompositeConverter;
import filter.Filters;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import processor.HttpService;

@Slf4j
public class App {
    public static void main(String[] args) {
        ClassFinder classFinder = ClassFinder.from(App.class, "com.main.business");
        List<Class<?>> clazzes = classFinder.findClazzes();
        Container container = new ComponentContainerCreator(clazzes).create();

        ResourceFinder resourceFinder = ResourceFinder.from(App.class, "resources");
        StaticResourceExecutor staticResourceExecutor = new StaticResourceExecutor(resourceFinder);

        ApplicationWebFilterCreator applicationWebFilterCreator = ApplicationWebFilterCreator.from(container, clazzes);
        Filters filters = applicationWebFilterCreator.create();

        MethodExecutor methodExecutor = new MethodExecutor(container);
        HttpPathMatcher httpPathMatcher = new ControllerHttpPathMatcherCreator(clazzes).create();
        CompositeConverter converter = new CompositeConverter();
        ApplicationRequestExecutor applicationRequestExecutor = new ApplicationRequestExecutor(methodExecutor, httpPathMatcher, converter);

        HttpService httpService = new HttpService(applicationRequestExecutor, staticResourceExecutor, filters);
        httpService.start();
    }
}