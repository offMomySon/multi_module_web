package instance;

import com.main.util.ClassFinder;
import instance.ObjectGraph.ReadOnlyObjectGraph;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedObjectRepositoryCreator {
    private final Annotations instantiateAnnotations;
    private final AnnotatedClassInstantiator annotatedClassInstantiator;

    private AnnotatedObjectRepositoryCreator(@NonNull Annotations instantiateAnnotations) {
        this.instantiateAnnotations = instantiateAnnotations;
        this.annotatedClassInstantiator = new AnnotatedClassInstantiator(instantiateAnnotations);
    }

    public AnnotatedObjectRepository creaet(@NonNull Class<?> rootClazz, @NonNull String classPackage) {
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

        return AnnotatedObjectRepository.of(objectGraph);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Annotations instantiateAnnotations;

        public Builder() {
        }

        public final Builder annotations(@NonNull Class<?>... annotations) {
            List<Class<?>> newAnnotations = Arrays.stream(annotations)
                .filter(Class::isAnnotation)
                .collect(Collectors.toUnmodifiableList());
            this.instantiateAnnotations = new Annotations(newAnnotations);
            return this;
        }

        public AnnotatedObjectRepositoryCreator build() {
            return new AnnotatedObjectRepositoryCreator(this.instantiateAnnotations);
        }
    }
}
