package filter;

import java.util.Objects;

public class FilterDef {
    private final String name;
    private final String pattern;

    public FilterDef(String name, String pattern) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        if (Objects.isNull(pattern) || pattern.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }
}
