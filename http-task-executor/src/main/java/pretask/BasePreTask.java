package pretask;

import task.pattern.PatternMatcher;
import java.util.Objects;
import java.util.Optional;
import pretask.pattern.PatternMatcherStrategy;
import task.PreTask;
import task.PreTaskWorker;

public class BasePreTask implements PreTask {
    private final String name;
    private final PatternMatcher patternMatcher;
    private final PreTaskWorker preTaskWorker;

    public BasePreTask(String name, PatternMatcher patternMatcher, PreTaskWorker preTaskWorker) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("name is empty.");
        }
        Objects.requireNonNull(patternMatcher);
        Objects.requireNonNull(preTaskWorker);

        this.name = name;
        this.patternMatcher = patternMatcher;
        this.preTaskWorker = preTaskWorker;
    }

    public static BasePreTask from(String name, PatternMatcherStrategy patternMatcherStrategy, PreTaskWorker preTaskWorker) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new RuntimeException("Does not exist name.");
        }
        Objects.requireNonNull(patternMatcherStrategy);
        Objects.requireNonNull(preTaskWorker);

        PatternMatcher patternMatcher = patternMatcherStrategy.create();
        return new BasePreTask(name, patternMatcher, preTaskWorker);
    }

    public static BasePreTask from2(PreTaskInfo preTaskInfo) {
        Objects.requireNonNull(preTaskInfo);

        String name = preTaskInfo.getName();
        PatternMatcherStrategy patternMatcherStrategy = new PatternMatcherStrategy(preTaskInfo.getPattern());
        PatternMatcher patternMatcher = patternMatcherStrategy.create();
        PreTaskWorker preTaskWorker = preTaskInfo.getPreTaskWorker();

        return new BasePreTask(name, patternMatcher, preTaskWorker);
    }

    public String getName() {
        return name;
    }

    public PreTaskWorker getFilterWorker() {
        return preTaskWorker;
    }

    public PatternMatcher getPatternMatcher() {
        return patternMatcher;
    }

    public boolean isMatchUrl(String requestUrl) {
        return matchUrl(requestUrl).isPresent();
    }

    public Optional<PreTaskWorker> matchUrl(String requestUrl) {
        if (Objects.isNull(requestUrl) || requestUrl.isBlank()) {
            throw new RuntimeException("requestUrl is empty.");
        }

        if (patternMatcher.isMatch(requestUrl)) {
            return Optional.of(preTaskWorker);
        }
        return Optional.empty();
    }
}
