package filter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterDef {
    private final String name;
    private final List<String> patterns;

    public FilterDef(String name, List<String> patterns) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patterns);
        patterns = patterns.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());

        if (patterns.isEmpty()) {
            throw new RuntimeException("patterns is empty.");
        }

        this.name = name;
        this.patterns = patterns;
    }

    public String getName() {
        return name;
    }

    public List<String> getPatterns() {
        return patterns;
    }
}
