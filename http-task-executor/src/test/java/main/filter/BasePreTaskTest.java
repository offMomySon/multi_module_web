package main.filter;

import pretask.BasePreTask;
import pretask.PreTaskWorker;
import pretask.pattern.PatternMatcher;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pretask.pattern.BasePatternMatcher;
import vo.HttpRequest;
import vo.HttpResponse;

class BasePreTaskTest {

    @DisplayName("url 이 매칭되면 Worker 를 가져옵니다.")
    @Test
    void test() throws Exception {
        //given
        String requestPath = "/base/path";
        PatternMatcher patternMatcher = new BasePatternMatcher(requestPath);
        BasePreTask basePreTask = new BasePreTask("filterName", patternMatcher, new TestPreTaskWorker());

        //when
        Optional<PreTaskWorker> optionalActual = basePreTask.matchUrl(requestPath);

        //then
        Assertions.assertThat(optionalActual).isPresent();
        PreTaskWorker actualWorker = optionalActual.get();
        Assertions.assertThat(actualWorker).isInstanceOf(TestPreTaskWorker.class);
    }

    @DisplayName("url 이 매칭되면 Worker 를 가져옵니다.")
    @Test
    void ttest() throws Exception {
        //given
        String requestPath = "/base/path";
        PatternMatcher patternMatcher = new BasePatternMatcher(requestPath + "/diffPath");
        BasePreTask basePreTask = new BasePreTask("filterName", patternMatcher, new TestPreTaskWorker());

        //when
        Optional<PreTaskWorker> optionalActual = basePreTask.matchUrl(requestPath);

        //then
        Assertions.assertThat(optionalActual).isEmpty();
    }


    public static class TestPreTaskWorker implements PreTaskWorker {
        @Override
        public boolean execute(HttpRequest httpRequest, HttpResponse httpResponse) {
            return true;
        }

        @Override
        public boolean postExecute(HttpRequest httpRequest, HttpResponse httpResponse) {
            return true;
        }
    }
}