package filter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterRegistration {
    private final String filterName;
    private final List<String> patterns;
    private final FilterWorker filterWorker;

    public FilterRegistration(String filterName, List<String> patterns, FilterWorker filterWorker) {
        if (Objects.isNull(filterName) || filterName.isBlank()) {
            throw new RuntimeException("filterName is empty.");
        }
        Objects.requireNonNull(patterns);
        Objects.requireNonNull(filterWorker);

        this.filterName = filterName;
        this.patterns = patterns.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        this.filterWorker = filterWorker;
    }

    public String getFilterName() {
        return filterName;
    }


    public List<String> getPatterns() {
        return patterns;
    }

    public FilterWorker getFilterWorker() {
        return filterWorker;
    }
}
