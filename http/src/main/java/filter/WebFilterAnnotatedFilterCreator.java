package filter;

import filter.annotation.WebFilter;
import filter.pattern.PatternMatcher;
import filter.pattern.PatternMatcherStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import util.AnnotationUtils;

public class WebFilterAnnotatedFilterCreator implements AbstractFilterCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final WebFilter webFilter;
    private final FilterWorker filterWorker;

    public WebFilterAnnotatedFilterCreator(FilterWorker filterWorker) {
        Objects.requireNonNull(filterWorker);

        Class<? extends FilterWorker> filterClazz = filterWorker.getClass();

        this.webFilter = AnnotationUtils.find(filterClazz, WEB_FILTER_CLASS)
            .orElseThrow(() -> new RuntimeException("filter does not annotated WebFilter."));
        this.filterWorker = filterWorker;
    }

    @Override
    public Filters create() {
        String filterName = webFilter.filterName().isEmpty() ? filterWorker.getClass().getSimpleName() : webFilter.filterName();
        List<String> basePaths = Arrays.stream(webFilter.patterns()).collect(Collectors.toUnmodifiableList());

        List<Filter> filters = basePaths.stream()
            .map(basePath -> createFilter(filterName, basePath, filterWorker))
            .collect(Collectors.toUnmodifiableList());

        return new Filters(filters);
    }

    private static Filter createFilter(String filterName, String basePath, FilterWorker filterWorker) {
        PatternMatcher patternMatcher = PatternMatcherStrategy.create(basePath);
        return new Filter(filterName, patternMatcher, filterWorker);
    }
}
