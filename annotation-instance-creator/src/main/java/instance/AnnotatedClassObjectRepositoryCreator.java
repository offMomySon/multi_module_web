package instance;

import annotation.AnnotationPropertyMapper;
import annotation.AnnotationPropertyMappers;
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
    private final AnnotationPropertyMappers annotationPropertyMappers;
    private final AnnotatedClassInstantiator annotatedClassInstantiator;

    private AnnotatedClassObjectRepositoryCreator(Annotations instantiateAnnotations, AnnotationPropertyMappers annotationPropertyMappers) {
        Objects.requireNonNull(instantiateAnnotations);
        Objects.requireNonNull(annotationPropertyMappers);
        this.instantiateAnnotations = instantiateAnnotations;
        this.annotationPropertyMappers = annotationPropertyMappers;
        this.annotatedClassInstantiator = new AnnotatedClassInstantiator(instantiateAnnotations);
    }

    public AnnotatedClassObjectRepository createFromPackage(Class<?> rootClazz, String classPackage) {
        if (Objects.isNull(rootClazz) || Objects.isNull(classPackage) || classPackage.isBlank()) {
            return AnnotatedClassObjectRepository.emtpy();
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

        return AnnotatedClassObjectRepository.from(this.annotationPropertyMappers, objectGraph);
    }

    public static Builder builderWithDefaultAnnotations() {
        return Builder.builderWithDefaultAnnotations();
    }

    public static class Builder {
        private Annotations annotations = Annotations.empty();
        private AnnotationPropertyMappers annotationPropertyMappers = AnnotationPropertyMappers.empty();

        private Builder(Annotations annotations) {
            Objects.requireNonNull(annotations);
            this.annotations = annotations;
        }

        public static Builder builderWithDefaultAnnotations() {
            return new Builder(DEFAULT_ANNOTATIONS);
        }

        public Builder annotationPropertyMappers(AnnotationPropertyMappers annotationPropertyMappers){
            Objects.requireNonNull(annotationPropertyMappers);
            this.annotationPropertyMappers = this.annotationPropertyMappers.merge(annotationPropertyMappers);
            return this;
        }

        public Builder annotations(Annotations annotations) {
            Objects.requireNonNull(annotations);
            this.annotations = this.annotations.merge(annotations);
            return this;
        }

        public AnnotatedClassObjectRepositoryCreator build() {
            return new AnnotatedClassObjectRepositoryCreator(this.annotations, this.annotationPropertyMappers);
        }
    }
}
