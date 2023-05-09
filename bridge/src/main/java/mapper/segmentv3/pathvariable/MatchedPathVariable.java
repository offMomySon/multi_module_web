package mapper.segmentv3.pathvariable;

import java.util.Objects;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariableValue;

public class MatchedPathVariable {
    private final PathUrl leftPathUrl;
    private final PathVariableValue pathVariableValue;

    public MatchedPathVariable(PathUrl leftPathUrl, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(leftPathUrl);
        Objects.requireNonNull(pathVariableValue);

        this.leftPathUrl = leftPathUrl;
        this.pathVariableValue = pathVariableValue;
    }

    public PathUrl getLeftPathUrl() {
        return leftPathUrl;
    }

    public PathVariableValue getPathVariable() {
        return pathVariableValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchedPathVariable that = (MatchedPathVariable) o;
        return Objects.equals(leftPathUrl, that.leftPathUrl) && Objects.equals(pathVariableValue, that.pathVariableValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftPathUrl, pathVariableValue);
    }

    @Override
    public String toString() {
        return "MatchPathVariable{" +
            "leftPathUrl=" + leftPathUrl +
            ", pathVariable=" + pathVariableValue +
            '}';
    }
}
