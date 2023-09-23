package instance;

import annotation.Component;
import annotation.Domain;
import annotation.Repository;
import annotation.Service;
import com.main.util.ClassFinder;
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

    public static AnnotatedClassObjectRepositoryCreator registCustomAnnotations(Annotations customAnnotations) {
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

        List<Class<?>> annotatedClazzes = clazzes.stream()
            .filter(Objects::nonNull)
            .filter(instantiateAnnotations::anyAnnotatedFrom)
            .collect(Collectors.toUnmodifiableList());

        ReadOnlyObjectRepository repository = ReadOnlyObjectRepository.empty();
        for (Class<?> clazz : annotatedClazzes) {
            repository = annotatedClassInstantiator.load(clazz, repository);
        }
        return repository;
    }
}
