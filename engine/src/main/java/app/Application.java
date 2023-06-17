package app;

import container.ComponentContainerCreator;
import container.Container;
import converter.base.CompositeConverter;
import executor.HttpStaticResourceExecutor;
import executor.MethodExecutor;
import executor.RequestExecutor;
import filter.ApplicationWebFilterCreator;
import filter.Filters;
import java.util.List;
import matcher.ControllerHttpPathMatcherCreator;
import matcher.HttpPathMatcher;
import processor.HttpService;
import resource.FileFinder;
import util.FileSystemUtil;

public class Application {

    public static void run(Class<?> baseClazz, String basePackage, String resourcePackage) {
        // 파일시스템에서 지정한 패키지 하위의 모든 클래스 파일을 가져옵니다.
        // 가져온 이유는 클래스의 메소드를 객체화 하기 위해서 입니다.

        FileFinder fileFinder = FileFinder.create(baseClazz, resourcePackage);
        HttpStaticResourceExecutor httpStaticResourceExecutor = new HttpStaticResourceExecutor(fileFinder);

        List<Class<?>> classes = FileSystemUtil.findClass(baseClazz, basePackage);

        Container container = new ComponentContainerCreator(classes).create();

        ApplicationWebFilterCreator applicationWebFilterCreator = ApplicationWebFilterCreator.from(container, classes);
        Filters filters = applicationWebFilterCreator.create();

        MethodExecutor methodExecutor = new MethodExecutor(container);
        HttpPathMatcher httpPathMatcher = new ControllerHttpPathMatcherCreator(classes).create();
        CompositeConverter converter = new CompositeConverter();
        RequestExecutor requestExecutor = new RequestExecutor(methodExecutor, httpPathMatcher, converter);

        HttpService httpService = new HttpService(requestExecutor, httpStaticResourceExecutor, filters);
        httpService.start();
    }
}
