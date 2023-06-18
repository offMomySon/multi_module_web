package app;

import container.ComponentContainerCreator;
import container.Container;
import converter.base.CompositeConverter;
import executor.ApplicationRequestExecutor;
import executor.MethodExecutor;
import executor.StaticResourceExecutor;
import filter.ApplicationWebFilterCreator;
import filter.Filters;
import java.util.List;
import java.util.Objects;
import matcher.ControllerHttpPathMatcherCreator;
import matcher.HttpPathMatcher;
import processor.HttpService;
import resource.ClassFinder;
import resource.ResourceFinder;

public class Application {

    public static void run(Class<?> baseClazz, String basePackage, String resourcePackage) {
        Objects.requireNonNull(baseClazz);
        if (Objects.isNull(basePackage) || basePackage.isBlank()) {
            throw new RuntimeException("basePackage is empty.");
        }
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("resourcePackage is empty.");
        }

        ClassFinder classFinder = ClassFinder.from(baseClazz, basePackage);
        List<Class<?>> clazzes = classFinder.findClazzes();
        Container container = new ComponentContainerCreator(clazzes).create();

        ResourceFinder resourceFinder = ResourceFinder.from(baseClazz, resourcePackage);
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
