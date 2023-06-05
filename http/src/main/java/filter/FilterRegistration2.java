package filter;

import java.util.Objects;

public class FilterRegistration2 {
    private final String filterName;
    private final String pattern;
    private final FilterWorker filterWorker;

    public FilterRegistration2(String filterName, String pattern, FilterWorker filterWorker) {
        if (Objects.isNull(filterName) || filterName.isBlank()) {
            throw new RuntimeException("filterName is empty.");
        }
        if (Objects.isNull(pattern) || pattern.isBlank()) {
            throw new RuntimeException("pattern is empty.");
        }
        Objects.requireNonNull(filterWorker);

        this.filterName = filterName;
        this.pattern = pattern;
        this.filterWorker = filterWorker;
    }

    public String getFilterName() {
        return filterName;
    }

    public String getPattern() {
        return pattern;
    }

    public FilterWorker getFilterWorker() {
        return filterWorker;
    }
}
