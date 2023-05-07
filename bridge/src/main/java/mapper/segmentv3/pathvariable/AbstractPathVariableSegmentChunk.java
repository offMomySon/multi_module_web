package mapper.segmentv3.pathvariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.SegmentChunk;

public abstract class AbstractPathVariableSegmentChunk implements SegmentChunk {
    private List<MatchedPathVariable> matchedPathVariables;

    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        Objects.requireNonNull(pathUrl);

        this.matchedPathVariables = internalConsume(pathUrl);

        return this.matchedPathVariables.stream()
            .map(MatchedPathVariable::getLeftPathUrl)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<MatchedPathVariable> getMatchedPathVariables() {
        return new ArrayList<>(matchedPathVariables);
    }

    public abstract List<MatchedPathVariable> internalConsume(PathUrl pathUrl);
}
