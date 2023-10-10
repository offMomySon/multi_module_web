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
        String pattern = preTaskInfo.getPattern();
        PreTaskWorker preTaskWorker = preTaskInfo.getPreTaskWorker();

        PatternMatcherStrategy patternMatcherStrategy = new PatternMatcherStrategy(pattern);
        PatternMatcher patternMatcher = patternMatcherStrategy.create();

        return new BasePreTask(name, patternMatcher, preTaskWorker);
    }
}

