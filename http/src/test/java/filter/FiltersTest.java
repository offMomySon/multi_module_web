package filter;

import filter.pattern.BasePatternMatcher;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vo.HttpRequest;
import vo.HttpResponse;

class FiltersTest {

    @DisplayName("동일한 필터 이름을 가진 filter 가 존재하는 filters 를 merge 하면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        String filterName = "sameFilterName";

        Filter filter1 = new Filter(filterName, new BasePatternMatcher("/basePath"), new FiltersTest.TestFilterWorker());
        Filters filters = new Filters(List.of(filter1));

        Filter filter2 = new Filter(filterName, new BasePatternMatcher("/basePath"), new FiltersTest.TestFilterWorker());
        Filters containSameFilterNameFilters = new Filters(List.of(filter2));

        //when
        Throwable actual = Assertions.catchThrowable(() -> filters.merge(containSameFilterNameFilters));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("url 패턴이 일치하는 filterWorker 들을 가져옵니다.")
    @Test
    void ttest() throws Exception {
        //given
        int expectSize = 3;
        String pattern = "/basePath";
        BasePatternMatcher basePatternMatcher = new BasePatternMatcher(pattern);
        List<Filter> filters = IntStream.range(0, expectSize)
            .mapToObj(n -> new Filter("filterName:" + n, basePatternMatcher, new FiltersTest.TestFilterWorker()))
            .collect(Collectors.toUnmodifiableList());

        String diffPattern = "/diffPath";
        BasePatternMatcher diffPatternMatcher = new BasePatternMatcher(diffPattern);
        Filter diffPathFilter = new Filter("diffPathFilter", diffPatternMatcher, new FiltersTest.TestFilterWorker());

        List<Filter> combinedFilters = Stream.of(filters, List.of(diffPathFilter)).flatMap(List::stream).collect(Collectors.toUnmodifiableList());
        Filters allFilters = new Filters(combinedFilters);

        //when
        List<FilterWorker> actuals = allFilters.findFilterWorkers(pattern);

        //then
        Assertions.assertThat(actuals).hasSize(expectSize);
        FilterWorker[] expectFilterWorkers = filters.stream().map(Filter::getFilterWorker2).collect(Collectors.toUnmodifiableList()).toArray(FilterWorker[]::new);
        Assertions.assertThat(actuals).containsOnly(expectFilterWorkers);
    }

    @DisplayName("url 패턴에 매칭된 n 개의 필터가 동일한 필터이름이면, 1개의 filterWorker 만 가져옵니다.")
    @Test
    void tttest() throws Exception {
        //given
        String pattern = "/basePath";
        BasePatternMatcher basePatternMatcher = new BasePatternMatcher(pattern);
        TestFilterWorker baseFilterWorker = new TestFilterWorker();
        List<Filter> filters = IntStream.range(0, 5)
            .mapToObj(n -> new Filter("sameFilterName", basePatternMatcher, baseFilterWorker))
            .collect(Collectors.toUnmodifiableList());

        TestFilterWorker otherFilterWorker = new TestFilterWorker();
        Filter diffPathFilter = new Filter("diffFilterName", basePatternMatcher, otherFilterWorker);

        List<Filter> combinedFilters = Stream.of(filters, List.of(diffPathFilter)).flatMap(List::stream).collect(Collectors.toUnmodifiableList());
        Filters allFilters = new Filters(combinedFilters);

        //when
        List<FilterWorker> actuals = allFilters.findFilterWorkers(pattern);

        //then
        Assertions.assertThat(actuals).hasSize(2);
        FilterWorker[] expectFilterWorkers = new FilterWorker[]{baseFilterWorker, otherFilterWorker};
        Assertions.assertThat(actuals).containsOnly(expectFilterWorkers);
    }

    public static class TestFilterWorker implements FilterWorker {
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