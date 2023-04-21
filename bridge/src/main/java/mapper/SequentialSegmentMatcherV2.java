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

public class SequentialSegmentMatcherV2 {
    private final Deque<SegmentMatcher> provider;
    private final List<MatchResult> prevResults;

    private SequentialSegmentMatcherV2(Deque<SegmentMatcher> provider, List<MatchResult> prevResults) {
        if (Objects.isNull(provider)) {
            throw new RuntimeException("provider is null");
        }
        if (Objects.isNull(prevResults)) {
            throw new RuntimeException("preResults is empty.");
        }

        this.provider = new ArrayDeque<>(provider);
        this.prevResults = prevResults.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    public static SequentialSegmentMatcherV2 bootStrap(Deque<SegmentMatcher> provider, String requestUrl) {
        if (Objects.isNull(requestUrl)) {
            throw new RuntimeException("prevMatchResults is null");
        }
        List<MatchResult> bootStrapResult = List.of(new MatchResult(requestUrl, RequestValues.empty()));

        return new SequentialSegmentMatcherV2(provider, bootStrapResult);
    }

    public List<MatchResult> match() {
        return doMatch(this.provider, prevResults);
    }

    private static List<MatchResult> doMatch(Deque<SegmentMatcher> provider, List<MatchResult> prevResults) {
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

        SequentialSegmentMatcherV2 nextMatcher = new SequentialSegmentMatcherV2(provider, newResults);
        return nextMatcher.match();
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
