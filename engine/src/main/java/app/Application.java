package app;

import container.ComponentContainerCreator;
import container.Container;
import converter.base.CompositeConverter;
import executor.StaticResourceExecutor;
import executor.MethodExecutor;
import executor.RequestExecutor;
import filter.ApplicationWebFilterCreator;
import filter.Filters;
import java.util.List;
import matcher.ControllerHttpPathMatcherCreator;
import matcher.HttpPathMatcher;
import processor.HttpService;
import resource.ClassFinder;
import resource.ResourceFinder;

public class Application {

    public static void run(Class<?> baseClazz, String basePackage, String resourcePackage) {
        ClassFinder classFinder = ClassFinder.from(baseClazz, basePackage);
        List<Class<?>> clazzes = classFinder.findClazzes();

        ResourceFinder resourceFinder = ResourceFinder.from(baseClazz, resourcePackage);
        StaticResourceExecutor staticResourceExecutor = new StaticResourceExecutor(resourceFinder);

        Container container = new ComponentContainerCreator(clazzes).create();

        ApplicationWebFilterCreator applicationWebFilterCreator = ApplicationWebFilterCreator.from(container, clazzes);
        Filters filters = applicationWebFilterCreator.create();

        MethodExecutor methodExecutor = new MethodExecutor(container);
        HttpPathMatcher httpPathMatcher = new ControllerHttpPathMatcherCreator(clazzes).create();
        CompositeConverter converter = new CompositeConverter();
        RequestExecutor requestExecutor = new RequestExecutor(methodExecutor, httpPathMatcher, converter);

        HttpService httpService = new HttpService(requestExecutor, staticResourceExecutor, filters);
        httpService.start();
    }
}
