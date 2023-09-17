package taskmatcher.segment;

import java.util.Objects;

public class PathVariableUtil {
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    public static boolean isPathVariable(String segment) {
        Objects.requireNonNull(segment);
        return segment.startsWith(PATH_VARIABLE_OPENER) && segment.endsWith(PATH_VARIABLE_CLOSER);
    }

    private static boolean doesNotPathVariable(String segment) {
        return !isPathVariable(segment);
    }

    public static String parsePathVariable(String segment) {
        if (doesNotPathVariable(segment)) {
            throw new RuntimeException("does not pathVaraible.");
        }
        return segment.substring(1, segment.length() - 1);
    }
}
