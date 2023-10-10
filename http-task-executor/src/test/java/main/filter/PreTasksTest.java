package main.filter;

import pretask.PreTask;
import pretask.BasePreTask;
import pretask.PreTaskWorker;
import pretask.PreTasks;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pretask.pattern.BasePatternMatcher;
import vo.HttpRequest;
import vo.HttpResponse;

class PreTasksTest {

    @DisplayName("동일한 필터 이름을 가진 filter 가 존재하는 filters 를 merge 하면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        String filterName = "sameFilterName";

        BasePreTask basePreTask1 = new BasePreTask(filterName, new BasePatternMatcher("/basePath"), new TestPreTaskWorker());
        PreTasks preTasks = new PreTasks(List.of(basePreTask1));

        BasePreTask basePreTask2 = new BasePreTask(filterName, new BasePatternMatcher("/basePath"), new TestPreTaskWorker());
        PreTasks containSameFilterNamePreTasks = new PreTasks(List.of(basePreTask2));

        //when
        Throwable actual = Assertions.catchThrowable(() -> preTasks.merge(containSameFilterNamePreTasks));

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
        List<BasePreTask> basePreTasks = IntStream.range(0, expectSize)
            .mapToObj(n -> new BasePreTask("filterName:" + n, basePatternMatcher, new TestPreTaskWorker()))
            .collect(Collectors.toUnmodifiableList());

        String diffPattern = "/diffPath";
        BasePatternMatcher diffPatternMatcher = new BasePatternMatcher(diffPattern);
        BasePreTask diffPathBasePreTask = new BasePreTask("diffPathFilter", diffPatternMatcher, new TestPreTaskWorker());

        List<PreTask> combinedFilters = Stream.of(basePreTasks, List.of(diffPathBasePreTask)).flatMap(List::stream).collect(Collectors.toUnmodifiableList());
        PreTasks.ReadOnlyPreTasks allFilters = new PreTasks(combinedFilters).lock();

        //when
        List<PreTaskWorker> actuals = allFilters.findFilterWorkers(pattern);

        //then
        Assertions.assertThat(actuals).hasSize(expectSize);
        PreTaskWorker[] expectPreTaskWorkers = basePreTasks.stream().map(BasePreTask::getFilterWorker).collect(Collectors.toUnmodifiableList()).toArray(PreTaskWorker[]::new);
        Assertions.assertThat(actuals).containsOnly(expectPreTaskWorkers);
    }

    @DisplayName("url 패턴에 매칭된 n 개의 필터가 동일한 필터이름이면, 1개의 filterWorker 만 가져옵니다.")
    @Test
    void tttest() throws Exception {
        //given
        String pattern = "/basePath";
        BasePatternMatcher basePatternMatcher = new BasePatternMatcher(pattern);
        TestPreTaskWorker baseFilterWorker = new TestPreTaskWorker();
        List<BasePreTask> basePreTasks = IntStream.range(0, 5)
            .mapToObj(n -> new BasePreTask("sameFilterName", basePatternMatcher, baseFilterWorker))
            .collect(Collectors.toUnmodifiableList());

        TestPreTaskWorker otherFilterWorker = new TestPreTaskWorker();
        BasePreTask diffPathBasePreTask = new BasePreTask("diffFilterName", basePatternMatcher, otherFilterWorker);

        List<PreTask> combinedFilters = Stream.of(basePreTasks, List.of(diffPathBasePreTask)).flatMap(List::stream).collect(Collectors.toUnmodifiableList());
        PreTasks.ReadOnlyPreTasks allFilters = new PreTasks(combinedFilters).lock();

        //when
        List<PreTaskWorker> actuals = allFilters.findFilterWorkers(pattern);

        //then
        Assertions.assertThat(actuals).hasSize(2);
        PreTaskWorker[] expectPreTaskWorkers = new PreTaskWorker[]{baseFilterWorker, otherFilterWorker};
        Assertions.assertThat(actuals).containsOnly(expectPreTaskWorkers);
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