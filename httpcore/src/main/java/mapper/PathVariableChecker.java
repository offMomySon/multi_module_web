package mapper;

import java.util.Objects;

public class PathVariableChecker {
    private final String PATH_VARIABLE_OPENER = "{";
    private final String PATH_VARIABLE_COLSER = "}";

    private final String path;

    public PathVariableChecker(String value) {
        this.path = value;
    }

    public boolean isPathVariable() {
        boolean pathVariable = path.startsWith(PATH_VARIABLE_OPENER) && path.endsWith(PATH_VARIABLE_COLSER);
        return pathVariable;
    }

    public boolean possibleMatchPath(String otherPath) {
        if(Objects.isNull(otherPath)){
            return false;
        }

        boolean emptyPatternRequestPath = otherPath.isEmpty() || otherPath.isBlank();

        if (emptyPatternRequestPath) {
            return false;
        }
        return true;
    }

    public boolean doesNotPossibleMatchPath(String otherPaht){
        return  !possibleMatchPath(otherPaht);
    }
}
