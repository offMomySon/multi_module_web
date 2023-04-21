package mapper;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.segment.SegmentMatcher;
import mapper.segment.SegmentMatcher.MatchResult;
import vo.RequestValues;

public class SequentialSegmentMatcher {
    private final Deque<SegmentMatcher> provider;

    public SequentialSegmentMatcher(Deque<SegmentMatcher> provider) {
        if (Objects.isNull(provider)) {
            throw new RuntimeException("provider is null");
        }

        this.provider = new ArrayDeque<>(provider);
    }

    public List<MatchResult> match(String requestUrl) {
        if (Objects.isNull(requestUrl)) {
            throw new RuntimeException("prevMatchResults is null");
        }
        List<MatchResult> bootStrapResult = List.of(new MatchResult(requestUrl, RequestValues.empty()));

        return doMatch(bootStrapResult);
    }

    private List<MatchResult> doMatch(List<MatchResult> prevResults) {
        boolean failMatch = !provider.isEmpty() && (Objects.isNull(prevResults) || prevResults.isEmpty());
        if (failMatch) {
            return Collections.emptyList();
        }

        boolean finishMatch = provider.isEmpty();
        if (finishMatch) {
            return prevResults.stream()
                .filter(MatchResult::isFinish)
                .collect(Collectors.toUnmodifiableList());
        }

        SegmentMatcher matcher = provider.pop();

        List<MatchResult> newResults = prevResults.stream()
            .map(prevResult -> doMatch(matcher, prevResult))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());

        return doMatch(newResults);
    }

    private static List<MatchResult> doMatch(SegmentMatcher matcher, MatchResult prevResult) {
        String leftPath = prevResult.getLeftPath();
        List<MatchResult> matchResults = matcher.match(leftPath);

        RequestValues prevPathVariable = prevResult.getPathVariable();
        return matchResults.stream()
            .map(result -> mergePathVariable(result, prevPathVariable))
            .collect(Collectors.toUnmodifiableList());
    }

    private static MatchResult mergePathVariable(MatchResult result, RequestValues prevPathVariable) {
        RequestValues resultPathVariable = result.getPathVariable();
        RequestValues mergeRequestValue = prevPathVariable.merge(resultPathVariable);

        return new MatchResult(result.getLeftPath(), mergeRequestValue);
    }
}
