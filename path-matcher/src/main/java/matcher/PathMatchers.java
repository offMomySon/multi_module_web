package matcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class PathMatchers<T> {
    private final Map<PathMatcher, T> values;

    public PathMatchers(Map<PathMatcher, T> values) {
        if (isNull(values)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

        this.values = values.entrySet().stream()
            .filter(entry -> nonNull(entry.getKey()))
            .filter(entry -> nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public Optional<MatchedElement> match(PathUrl pathUrl) {
        if (isNull(pathUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

        for (Map.Entry<PathMatcher, T> entry : values.entrySet()) {
            PathMatcher pathMatcher = entry.getKey();

            Optional<PathVariable> optionalPathVariable = pathMatcher.match(pathUrl);
            boolean failMatch = optionalPathVariable.isEmpty();
            if (failMatch) {
                continue;
            }

            T element = entry.getValue();
            PathVariable pathVariable = optionalPathVariable.get();
            MatchedElement<T> matchedElement = new MatchedElement<>(element, pathVariable);
            return Optional.of(matchedElement);
        }

        return Optional.empty();
    }


    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class MatchedElement<T> {
        private final T element;
        private final PathVariable pathVariable;

        public MatchedElement(T element, PathVariable pathVariable) {
            if (isNull(element) || isNull(pathVariable)) {
                throw new RuntimeException("Ensure the parameter is not null.");
            }
            this.element = element;
            this.pathVariable = pathVariable;
        }

        public T getElement() {
            return element;
        }

        public PathVariable getPathVariable() {
            return pathVariable;
        }
    }

    public static class Builder<T> {
        private final Map<PathMatcher, T> values = new HashMap<>();

        public Builder() {
        }

        public Builder<T> append(PathMatcher pathMatcher, T t) {
            if (isNull(pathMatcher) || isNull(t)) {
                throw new RuntimeException("Ensure the parameter is not null.");
            }

            values.put(pathMatcher, t);
            return this;
        }

        public PathMatchers<T> build() {
            return new PathMatchers<>(this.values);
        }
    }
}