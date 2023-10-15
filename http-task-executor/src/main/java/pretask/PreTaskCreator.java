package pretask;

import task.pattern.PatternMatcher;
import pretask.pattern.PatternMatcherStrategy;
import java.util.Objects;
import task.PreTaskWorker;

public class PreTaskCreator {

    public static BasePreTask create(PreTaskInfo preTaskInfo) {
        if (Objects.isNull(preTaskInfo)) {
            throw new RuntimeException("filterInfo is emtpy.");
        }
        String name = preTaskInfo.getName();
        PatternMatcherStrategy patternMatcherStrategy = new PatternMatcherStrategy(preTaskInfo.getPattern());
        PreTaskWorker preTaskWorker = preTaskInfo.getPreTaskWorker();

        return null;
//        return BasePreTask.from2(name, patternMatcherStrategy, preTaskWorker);
    }
}

