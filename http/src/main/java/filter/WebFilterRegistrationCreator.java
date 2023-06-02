package filter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import util.AnnotationUtils;

public class WebFilterRegistrationCreator extends AbstractFilterRegistrationCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final WebFilter webFilter;
    private final Filter filter;

    public WebFilterRegistrationCreator(Filter filter) {
        Objects.requireNonNull(filter);

        Class<? extends Filter> filterClazz = filter.getClass();

        Optional<WebFilter> optionalWebFilter = AnnotationUtils.find(filterClazz, WEB_FILTER_CLASS);
        boolean doesNotExistWebFilter = optionalWebFilter.isEmpty();
        if (doesNotExistWebFilter) {
            throw new RuntimeException("filter does not annotated WebFilter.");
        }

        this.webFilter = optionalWebFilter.get();
        this.filter = filter;
    }

    @Override
    public FilterRegistration create() {
        String filterName = Optional.of(webFilter.filterName())
            .orElseGet(() -> webFilter.getClass().getSimpleName());
        List<String> patterns = Arrays.stream(webFilter.patterns()).collect(Collectors.toUnmodifiableList());

        return new FilterRegistration(filterName, patterns, filter);
    }
}
