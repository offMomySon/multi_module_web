package filter;

import annotation.WebFilter;
import com.main.util.AnnotationUtils;
import filter.pattern.PatternMatcherStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebFilterAnnotatedFilterCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final WebFilter webFilter;
    private final FilterWorker filterWorker;

    public WebFilterAnnotatedFilterCreator(FilterWorker filterWorker) {
        Objects.requireNonNull(filterWorker);

        Class<? extends FilterWorker> filterClazz = filterWorker.getClass();
        this.webFilter = AnnotationUtils.find(filterClazz, WEB_FILTER_CLASS).orElseThrow(() -> new RuntimeException("For create Filter, FilterWorker must exist WebFilter annotation."));
        this.filterWorker = filterWorker;
    }

    public Filters create() {
        String name = webFilter.filterName().isEmpty() ? filterWorker.getClass().getSimpleName() : webFilter.filterName();
        List<PatternMatcherStrategy> strategies = Arrays.stream(webFilter.patterns())
            .map(PatternMatcherStrategy::new)
            .collect(Collectors.toUnmodifiableList());

        List<Filter> filters = strategies.stream()
            .map(strategy -> Filter.from(name, strategy, filterWorker))
            .collect(Collectors.toUnmodifiableList());
        return new Filters(filters);
    }
}
