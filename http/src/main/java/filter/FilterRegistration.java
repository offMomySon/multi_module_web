package filter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterRegistration {
    private final String filterName;
    private final List<String> patterns;
    private final Filter filter;

    public FilterRegistration(String filterName, List<String> patterns, Filter filter) {
        if (Objects.isNull(filterName) || filterName.isBlank()) {
            throw new RuntimeException("filterName is empty.");
        }
        Objects.requireNonNull(patterns);
        Objects.requireNonNull(filter);

        this.filterName = filterName;
        this.patterns = patterns.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        this.filter = filter;
    }

    public String getFilterName() {
        return filterName;
    }


    public List<String> getPatterns() {
        return patterns;
    }

    public Filter getFilter() {
        return filter;
    }
}
