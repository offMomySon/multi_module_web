package filter;

import filter.pattern.PatternUrl;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import util.AnnotationUtils;

public class WebFilterRegistrationCreator extends AbstractFilterRegistrationCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final WebFilter webFilter;
    private final FilterWorker filterWorker;

    public WebFilterRegistrationCreator(FilterWorker filterWorker) {
        Objects.requireNonNull(filterWorker);

        Class<? extends FilterWorker> filterClazz = filterWorker.getClass();
        WebFilter webFilter = AnnotationUtils.find(filterClazz, WEB_FILTER_CLASS)
            .orElseThrow(() -> new RuntimeException("filter does not annotated WebFilter."));

        this.webFilter = webFilter;
        this.filterWorker = filterWorker;
    }

    @Override
    public Filters create() {
        String filterName = Optional.of(webFilter.filterName())
            .orElseGet(() -> webFilter.getClass().getSimpleName());
        List<String> patterns = Arrays.stream(webFilter.patterns()).collect(Collectors.toUnmodifiableList());

        List<Filter> filters = patterns.stream()
            .map(pattern -> new Filter(filterName, new PatternUrl(pattern), filterWorker))
            .collect(Collectors.toUnmodifiableList());

        return Filters.from(filters);
    }
}
