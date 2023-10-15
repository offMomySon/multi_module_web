package pretask;

import java.util.Objects;
import java.util.Optional;
import pretask.pattern.PatternMatcherStrategy;
import task.PostTask;
import task.PostTaskWorker;
import task.PreTask;
import task.PreTaskWorker;
import task.pattern.PatternMatcher;

public class BasePostTask implements PostTask {
    private final String name;
    private final PatternMatcher patternMatcher;
    private final PostTaskWorker postTaskWorker;

    public BasePostTask(String name, PatternMatcher patternMatcher, PostTaskWorker postTaskWorker) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patternMatcher);
        Objects.requireNonNull(postTaskWorker);

        this.name = name;
        this.patternMatcher = patternMatcher;
        this.postTaskWorker = postTaskWorker;
    }

    public static BasePostTask from(String name, PatternMatcherStrategy patternMatcherStrategy, PostTaskWorker postTaskWorker) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("Does not exist name.");
        }
        Objects.requireNonNull(patternMatcherStrategy);
        Objects.requireNonNull(postTaskWorker);

        PatternMatcher patternMatcher = patternMatcherStrategy.create();
        return new BasePostTask(name, patternMatcher, postTaskWorker);
    }

    public static BasePostTask from2(PostTaskInfo postTaskInfo) {
        Objects.requireNonNull(postTaskInfo);

        String name = postTaskInfo.getName();
        String pattern = postTaskInfo.getPattern();
        PatternMatcherStrategy patternMatcherStrategy = new PatternMatcherStrategy(pattern);
        PatternMatcher patternMatcher = patternMatcherStrategy.create();
        PostTaskWorker postTaskWorker = postTaskInfo.getPostTaskWorker();

        return new BasePostTask(name, patternMatcher, postTaskWorker);
    }

    public String getName() {
        return name;
    }

    public PostTaskWorker getFilterWorker() {
        return postTaskWorker;
    }

    public PatternMatcher getPatternMatcher() {
        return patternMatcher;
    }

    public boolean isMatchUrl(String requestUrl) {
        return matchUrl(requestUrl).isPresent();
    }

    public Optional<PostTaskWorker> matchUrl(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        if (patternMatcher.isMatch(requestUrl)) {
            return Optional.of(postTaskWorker);
        }
        return Optional.empty();
    }
}
