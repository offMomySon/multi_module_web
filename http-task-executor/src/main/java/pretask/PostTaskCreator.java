package pretask;

import java.util.Objects;
import pretask.pattern.PatternMatcherStrategy;
import task.PostTaskWorker;
import task.PreTaskWorker;
import task.pattern.PatternMatcher;

public class PostTaskCreator {

    public static BasePostTask create(PostTaskInfo postTaskInfo) {
        if (Objects.isNull(postTaskInfo)) {
            throw new RuntimeException("filterInfo is emtpy.");
        }

        String name = postTaskInfo.getName();
        String pattern = postTaskInfo.getPattern();
        PostTaskWorker postTaskWorker = postTaskInfo.getPostTaskWorker();

        PatternMatcherStrategy patternMatcherStrategy = new PatternMatcherStrategy(pattern);
        PatternMatcher patternMatcher = patternMatcherStrategy.create();

        return new BasePostTask(name, patternMatcher, postTaskWorker);
    }
}

