package filter;

import annotation.WebFilter;
import filter.pattern.BasePatternMatcher;
import filter.pattern.PatternMatcher;
import filter.pattern.WildCardFileExtensionMatcher;
import filter.pattern.WildCardPathMatcher;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vo.HttpRequest;
import vo.HttpResponse;

class WebFilterAnnotatedFilterCreatorTest {

    @DisplayName("webfilter 어노테이션이 붙은 FilterWorker 를 필터로 생성합니다.")
    @Test
    void test() throws Exception {
        //given
        FilterWorker filterWorker = new WebFilterAnnotatedFilterWorker();
        WebFilterAnnotatedFilterCreator filterCreator = new WebFilterAnnotatedFilterCreator(filterWorker);

        //when
        List<Filter> actuals = filterCreator.create().getValues();

        //then
        Assertions.assertThat(actuals).hasSize(3);

        List<String> filterName = actuals.stream().map(Filter::getName).collect(Collectors.toUnmodifiableList());
        Assertions.assertThat(filterName).containsOnly("testFilterWorker");

        List<Class<? extends PatternMatcher>> patternMatcherClazzs = actuals.stream().map(Filter::getPatternMatcher).map(PatternMatcher::getClass).collect(Collectors.toUnmodifiableList());
        Assertions.assertThat(patternMatcherClazzs).containsOnly(BasePatternMatcher.class, WildCardFileExtensionMatcher.class, WildCardPathMatcher.class);
    }

    @WebFilter(filterName = "testFilterWorker", patterns = {"/p1", "/p2/*", "*.txt"})
    public static class WebFilterAnnotatedFilterWorker implements FilterWorker {
        @Override
        public boolean prevExecute(HttpRequest httpRequest, HttpResponse httpResponse) {
            return true;
        }

        @Override
        public boolean postExecute(HttpRequest httpRequest, HttpResponse httpResponse) {
            return true;
        }
    }

}