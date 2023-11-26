package matcher.path;

import static java.util.Objects.isNull;

public class PathUtil {
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    public static boolean isPathVariable(String segment) {
        if (isNull(segment)) {
            throw new RuntimeException("Must parameter not be null");
        }
        return segment.startsWith(PATH_VARIABLE_OPENER) && segment.endsWith(PATH_VARIABLE_CLOSER);
    }

    private static boolean doesNotPathVariable(String segment) {
        return !isPathVariable(segment);
    }

    public static String parsePathVariable(String segment) {
        if (doesNotPathVariable(segment)) {
            throw new RuntimeException("Does not path variable.");
        }
        return segment.substring(1, segment.length() - 1);
    }
}