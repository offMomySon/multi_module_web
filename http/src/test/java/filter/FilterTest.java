package filter;

import filter.pattern.BasePatternMatcher;
import filter.pattern.PatternMatcher;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vo.HttpRequest;
import vo.HttpResponse;

class FilterTest {

    @DisplayName("url 이 매칭되면 Worker 를 가져옵니다.")
    @Test
    void test() throws Exception {
        //given
        String requestPath = "/base/path";
        PatternMatcher patternMatcher = new BasePatternMatcher(requestPath);
        Filter filter = new Filter("filterName", patternMatcher, new TestFilterWorker());

        //when
        Optional<FilterWorker2> optionalActual = filter.matchUrl(requestPath);

        //then
        Assertions.assertThat(optionalActual).isPresent();
        FilterWorker2 actualWorker = optionalActual.get();
        Assertions.assertThat(actualWorker).isInstanceOf(TestFilterWorker.class);
    }

    @DisplayName("url 이 매칭되면 Worker 를 가져옵니다.")
    @Test
    void ttest() throws Exception {
        //given
        String requestPath = "/base/path";
        PatternMatcher patternMatcher = new BasePatternMatcher(requestPath + "/diffPath");
        Filter filter = new Filter("filterName", patternMatcher, new TestFilterWorker());

        //when
        Optional<FilterWorker2> optionalActual = filter.matchUrl(requestPath);

        //then
        Assertions.assertThat(optionalActual).isEmpty();
    }


    public static class TestFilterWorker implements FilterWorker2 {
        @Override
        public void prevExecute(HttpRequest httpRequest, HttpResponse httpResponse) {

        }

        @Override
        public void postExecute(HttpRequest httpRequest, HttpResponse httpResponse) {

        }
    }
}