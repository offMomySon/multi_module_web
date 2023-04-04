package mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import marker.Controller;

@Slf4j
public class HttpPathMatcherCreator {

    public static HttpPathMatcherIf create(List<Class<?>> classes) {
        List<Class<?>> controllerClazzs = classes.stream()
            .filter(clazz -> AnnotationUtils.exist(clazz, Controller.class))
            .collect(Collectors.toUnmodifiableList());

        List<HttpPathMatcher> httpPathMatchers = controllerClazzs.stream()
            .map(JavaMethodResolverCreator::new)
            .map(JavaMethodResolverCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("httpPathMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());

        return new CompositedHttpPathMatcher(httpPathMatchers);
    }
}
