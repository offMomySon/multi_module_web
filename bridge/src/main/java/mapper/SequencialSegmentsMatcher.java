package mapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import mapper.segment.SegmentsMatcher;
import mapper.segment.SegmentsMatcher.MatchResult;
import vo.RequestValues;

public class SequencialSegmentsMatcher {
    private final Deque<SegmentsMatcher> provider;
    private final List<MatchResult> prevMatchResults;

    public SequencialSegmentsMatcher(Deque<SegmentsMatcher> provider, List<MatchResult> prevMatchResults) {
        if (Objects.isNull(provider)) {
            throw new RuntimeException("provider is null");
        }
        if (Objects.isNull(prevMatchResults)) {
            throw new RuntimeException("prevMatchResults is null");
        }

        this.provider = new ArrayDeque<>(provider);
        this.prevMatchResults = prevMatchResults.stream().collect(Collectors.toUnmodifiableList());
    }

    public static SequencialSegmentsMatcher from(Deque<SegmentsMatcher> provider, String requestUrl) {
        if (Objects.isNull(provider)) {
            throw new RuntimeException("provider is null");
        }
        if (Objects.isNull(requestUrl)) {
            throw new RuntimeException("prevMatchResults is null");
        }

        List<MatchResult> bootStrapMatchResult = List.of(new MatchResult(requestUrl, RequestValues.empty()));

        return new SequencialSegmentsMatcher(provider, bootStrapMatchResult);
    }

    public Optional<RequestValues> match() {
        boolean doesNotExistRequestUrl = !provider.isEmpty() && (Objects.isNull(prevMatchResults) || prevMatchResults.isEmpty());
        if (doesNotExistRequestUrl) {
            return Optional.empty();
        }

        boolean doesNotLeftSegmentMatcher = provider.isEmpty();
        if (doesNotLeftSegmentMatcher) {
            return prevMatchResults.stream().filter(MatchResult::isFinish).findFirst().map(MatchResult::getPathVariable);
        }

        SegmentsMatcher matcher = provider.pop();

        List<MatchResult> newMatchResults = new ArrayList<>();
        for (MatchResult prevMatchResult : prevMatchResults) {
            String leftPath = prevMatchResult.getLeftPath();
            RequestValues prevPathVariable = prevMatchResult.getPathVariable();

            List<MatchResult> matchResults = matcher.match(leftPath);

            List<MatchResult> mergeMatchResults = mergeMatchResults(matchResults, prevPathVariable);
            newMatchResults.addAll(mergeMatchResults);
        }

        boolean doesNotExistMatchResult = newMatchResults.isEmpty();
        if (doesNotExistMatchResult) {
            return Optional.empty();
        }

        SequencialSegmentsMatcher sequencialSegmentsMatcher = new SequencialSegmentsMatcher(provider, newMatchResults);
        return sequencialSegmentsMatcher.match();
    }

    private static List<MatchResult> mergeMatchResults(List<MatchResult> matchResults, RequestValues prevPathVariable) {
        return matchResults.stream()
            .map(result -> {
                RequestValues resultPathVariable = result.getPathVariable();
                RequestValues mergeRequestValue = prevPathVariable.merge(resultPathVariable);

                return new MatchResult(result.getLeftPath(), mergeRequestValue);
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
