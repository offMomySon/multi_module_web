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
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedClassObjectRepositoryCreator2 {
    private static final Annotations DEFAULT_ANNOTATIONS = new Annotations(List.of(Component.class, Domain.class, Repository.class, Service.class));

    private final Annotations instantiateAnnotations;
    private final AnnotatedClassInstantiator annotatedClassInstantiator;
    private final AnnotationPropertyGetter annotationPropertyGetter;

    private AnnotatedClassObjectRepositoryCreator2(@NonNull Annotations instantiateAnnotations, @NonNull AnnotationPropertyGetter annotationPropertyGetter) {
        this.instantiateAnnotations = instantiateAnnotations;
        this.annotatedClassInstantiator = new AnnotatedClassInstantiator(instantiateAnnotations);
        this.annotationPropertyGetter = annotationPropertyGetter;
    }

    public AnnotatedClassObjectRepository fromPackage(@NonNull Class<?> rootClazz, @NonNull String classPackage) {
        if (classPackage.isBlank()) {
            throw new RuntimeException("Invalid param. Param is empty.");
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

        return AnnotatedClassObjectRepository.from(this.annotationPropertyGetter, objectGraph);
    }

    public static Builder builderWithDefaultAnnotations() {
        return new Builder(DEFAULT_ANNOTATIONS);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Annotations annotations;
        private AnnotationPropertyGetter annotationPropertyGetter;

        public Builder() {
        }

        private Builder(@NonNull Annotations annotations) {
            this.annotations = annotations;
        }

        public Builder annotations(@NonNull Annotations annotations) {
            if (Objects.isNull(this.annotations)) {
                this.annotations = annotations;
            }
            this.annotations = this.annotations.merge(annotations);
            return this;
        }

        public Builder annotationPropertyGetter(@NonNull AnnotationPropertyGetter annotationPropertyGetter) {
            this.annotationPropertyGetter = annotationPropertyGetter;
            return this;
        }

        public AnnotatedClassObjectRepositoryCreator2 build() {
            return new AnnotatedClassObjectRepositoryCreator2(this.annotations, this.annotationPropertyGetter);
        }
    }
}
