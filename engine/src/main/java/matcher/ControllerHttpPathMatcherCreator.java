package matcher;

import annotation.Controller;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import matcher.creator.JavaMethodPathMatcherCreator;
import util.AnnotationUtils;


@Slf4j
public class ControllerHttpPathMatcherCreator {
    private static final Class<Controller> CONTROLLER_CLASS = Controller.class;

    private final List<Class<?>> controllerClazzs;

    public ControllerHttpPathMatcherCreator(List<Class<?>> classes) {
        Objects.requireNonNull(classes, "classes is null.");

        this.controllerClazzs = classes.stream()
            .filter(clazz -> !Objects.isNull(clazz))
            .filter(clazz -> AnnotationUtils.exist(clazz, CONTROLLER_CLASS))
            .collect(Collectors.toUnmodifiableList());
    }

    public HttpPathMatcher create() {
        List<BaseHttpPathMatcher> baseHttpPathMatchers = controllerClazzs.stream()
            .map(JavaMethodPathMatcherCreator::new)
            .map(JavaMethodPathMatcherCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("httpPathMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());

        return new CompositedHttpPathMatcher(baseHttpPathMatchers);
    }
}
