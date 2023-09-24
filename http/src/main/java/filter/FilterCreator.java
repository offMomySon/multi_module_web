package filter;

import filter.pattern.PatternMatcher;
import filter.pattern.PatternMatcherStrategy;
import java.util.Objects;

public class FilterCreator {

    public static Filter create(FilterInfo filterInfo) {
        if (Objects.isNull(filterInfo)) {
            throw new RuntimeException("filterInfo is emtpy.");
        }

        String name = filterInfo.getName();
        String pattern = filterInfo.getPattern();
        FilterWorker filterWorker = filterInfo.getFilterWorker();

        PatternMatcherStrategy patternMatcherStrategy = new PatternMatcherStrategy(pattern);
        PatternMatcher patternMatcher = patternMatcherStrategy.create();

        return new Filter(name, patternMatcher, filterWorker);
    }
}

