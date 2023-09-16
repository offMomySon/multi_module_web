package instance;

import annotation.Component;
import annotation.Domain;
import annotation.Repository;
import annotation.Service;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotatedClassObjectRepositoryCreator {
    private static final Annotations DEFAULT_ANNOTATIONS = new Annotations(List.of(Component.class, Domain.class, Repository.class, Service.class));

    private final Annotations instantiateAnnotations;

    private AnnotatedClassObjectRepositoryCreator(Annotations instantiateAnnotations) {
        Objects.requireNonNull(instantiateAnnotations);
        this.instantiateAnnotations = instantiateAnnotations;
    }

    public static AnnotatedClassObjectRepositoryCreator appendCustomAnnotations(Annotations customAnnotations) {
        if (Objects.isNull(customAnnotations)) {
            return new AnnotatedClassObjectRepositoryCreator(DEFAULT_ANNOTATIONS);
        }
        Annotations annotations = DEFAULT_ANNOTATIONS.merge(customAnnotations);
        return new AnnotatedClassObjectRepositoryCreator(annotations);
    }

    public ReadOnlyObjectRepository create(List<Class<?>> clazzes) {
        if (Objects.isNull(clazzes)) {
            clazzes = Collections.emptyList();
        }

        clazzes = clazzes.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());

        ReadOnlyObjectRepository repository = ReadOnlyObjectRepository.empty();
        for (Class<?> clazz : clazzes) {
            AnnotatedClassInstantiator annotatedClassInstantiator = new AnnotatedClassInstantiator(instantiateAnnotations);
            repository = annotatedClassInstantiator.load(clazz, repository);
        }

        return repository;
    }
}
