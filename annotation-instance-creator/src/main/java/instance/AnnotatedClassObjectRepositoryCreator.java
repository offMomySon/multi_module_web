package instance;

import annotation.Component;
import annotation.Domain;
import annotation.Repository;
import annotation.Service;
import com.main.util.ClassFinder;
import instance.ObjectGraph.ReadOnlyObjectGraph;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedClassObjectRepositoryCreator {
    private static final Annotations DEFAULT_ANNOTATIONS = new Annotations(List.of(Component.class, Domain.class, Repository.class, Service.class));

    private final Annotations instantiateAnnotations;
    private final AnnotatedClassInstantiator annotatedClassInstantiator;

    private AnnotatedClassObjectRepositoryCreator(Annotations instantiateAnnotations) {
        Objects.requireNonNull(instantiateAnnotations);
        this.instantiateAnnotations = instantiateAnnotations;
        this.annotatedClassInstantiator = new AnnotatedClassInstantiator(instantiateAnnotations);
    }

    public static AnnotatedClassObjectRepositoryCreator appendAnnotations(Annotations customAnnotations) {
        if (Objects.isNull(customAnnotations)) {
            return new AnnotatedClassObjectRepositoryCreator(DEFAULT_ANNOTATIONS);
        }
        Annotations annotations = DEFAULT_ANNOTATIONS.merge(customAnnotations);
        return new AnnotatedClassObjectRepositoryCreator(annotations);
    }

    public ReadOnlyObjectRepository createFromPackage(Class<?> rootClazz, String classPackage) {
        if (Objects.isNull(rootClazz) || Objects.isNull(classPackage) || classPackage.isBlank()) {
            return ReadOnlyObjectRepository.empty();
        }

        List<Class<?>> clazzes = ClassFinder.from(rootClazz, classPackage).findClazzes();
        log.info("clazzes : {}", clazzes);

        List<Class<?>> annotatedClasses = clazzes.stream()
            .filter(Objects::nonNull)
            .filter(instantiateAnnotations::anyAnnotatedFrom)
            .collect(Collectors.toUnmodifiableList());

        ReadOnlyObjectGraph objectGraph = ReadOnlyObjectGraph.empty();
        for (Class<?> clazz : annotatedClasses) {
            objectGraph = annotatedClassInstantiator.load(clazz, objectGraph);
        }



        return null;
    }
}
