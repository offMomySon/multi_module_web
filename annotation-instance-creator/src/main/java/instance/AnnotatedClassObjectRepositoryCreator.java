package instance;

import annotation.Component;
import annotation.Domain;
import annotation.Repository;
import annotation.Service;
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

    public static AnnotatedClassObjectRepositoryCreator defaultAndCustomAnnotation(Annotations customAnnotations) {
        if (Objects.isNull(customAnnotations)) {
            return new AnnotatedClassObjectRepositoryCreator(DEFAULT_ANNOTATIONS);
        }
        Annotations annotations = DEFAULT_ANNOTATIONS.merge(customAnnotations);
        return new AnnotatedClassObjectRepositoryCreator(annotations);
    }

    public ObjectRepository create(List<Class<?>> clazzes) {
        clazzes = clazzes.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());

        ObjectRepository repository = ObjectRepository.empty();
        for(Class<?> clazz : clazzes){
            AnnotatedClassInstantiator annotatedClassInstantiator = new AnnotatedClassInstantiator(repository, instantiateAnnotations);
            repository = annotatedClassInstantiator.load(clazz);
        }

        return repository;
    }
}
