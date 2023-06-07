package filter;

import filter.annotation.WebFilter;
import filter.pattern.PatternMatcher;
import filter.pattern.PatternMatcherStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import util.AnnotationUtils;

//todo
// filterWorker class 로만 filter 를 생성하기 위해서는 (to be),
// filterWorker 내부의 instance class 들을 생성할 수 있어야한다.
// 생성된 filterWorker 를 가져와서 filter 를 생성한다 (as is).
// 이런 단점 때문에 tomcat 에서는 FilterDep 만 가지고 있다 필요한 순간 instance 를 가져오는듯.
public class WebFilterAnnotatedFilterCreator extends AbstractFilterCreator {
    private static final Class<WebFilter> WEB_FILTER_CLASS = WebFilter.class;

    private final WebFilter webFilter;
    private final FilterWorker filterWorker;

    public WebFilterAnnotatedFilterCreator(FilterWorker filterWorker) {
        Objects.requireNonNull(filterWorker);

        Class<? extends FilterWorker> filterClazz = filterWorker.getClass();

        this.webFilter = AnnotationUtils.find(filterClazz, WEB_FILTER_CLASS).orElseThrow(() -> new RuntimeException("filter does not annotated WebFilter."));
        this.filterWorker = filterWorker;
    }

    @Override
    public Filters create() {
        String filterName = Optional.of(webFilter.filterName()).orElseGet(() -> webFilter.getClass().getSimpleName());
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
