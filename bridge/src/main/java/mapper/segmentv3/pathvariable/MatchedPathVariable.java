package mapper.segmentv3.pathvariable;

import java.util.Objects;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariable;

public class MatchedPathVariable {
    private final PathUrl leftPathUrl;
    private final PathVariable pathVariable;

    public MatchedPathVariable(PathUrl leftPathUrl, PathVariable pathVariable) {
        Objects.requireNonNull(leftPathUrl);
        Objects.requireNonNull(pathVariable);

        this.leftPathUrl = leftPathUrl;
        this.pathVariable = pathVariable;
    }

    public PathUrl getLeftPathUrl() {
        return leftPathUrl;
    }

    public PathVariable getPathVariable() {
        return pathVariable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchedPathVariable that = (MatchedPathVariable) o;
        return Objects.equals(leftPathUrl, that.leftPathUrl) && Objects.equals(pathVariable, that.pathVariable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftPathUrl, pathVariable);
    }

    @Override
    public String toString() {
        return "MatchPathVariable{" +
            "leftPathUrl=" + leftPathUrl +
            ", pathVariable=" + pathVariable +
            '}';
    }
}
