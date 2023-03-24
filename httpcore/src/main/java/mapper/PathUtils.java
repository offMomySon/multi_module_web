package mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PathUtils {
    private static final String EMPTY_PATTERN = "";
    private static final String WILD_PATTER = "**";
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    public static boolean isPathVariable(String path) {
        return PathUtils.enclosedBy(PATH_VARIABLE_OPENER, PATH_VARIABLE_CLOSER, path);
    }

    public static boolean isLeftElementsAfterIndex(List<String> paths, int index) {
        return index < paths.size() - 1;
    }

    public static boolean onlyRemainWildCard(List<String> paths, int index) {
        return onlyRemainPattern(WILD_PATTER, paths, index);
    }

    public static boolean onlyRemainPattern(String comparePattern, List<String> paths, int index) {
        String path = paths.get(index);

        if (doesNotMatchPattern(comparePattern, path)) {
            return false;
        }

        if (isLeftElementsAfterIndex(paths, index)) {
            return false;
        }

        return true;
    }

    public static boolean outOfIndex(List<String> paths, int index) {
        if (Objects.isNull(paths)) {
            return true;
        }

        return paths.size() <= index;
    }

    public static boolean doesNotMatchPattern(String comparePattern, String path) {
        return !matchPattern(comparePattern, path);
    }

    public static boolean doesNotEmptyPattern(String path) {
        return !emptyPattern(path);
    }

    public static boolean doesNotWildCardPattern(String path) {
        return !wildCardPattern(path);
    }

    public static boolean emptyPattern(String path) {
        return matchPattern(EMPTY_PATTERN, path);
    }

    public static boolean wildCardPattern(String path) {
        return matchPattern(WILD_PATTER, path);
    }

    public static boolean matchPattern(String comparePattern, String path) {
        if (Objects.isNull(path)) {
            return false;
        }

        return Objects.equals(path, comparePattern);
    }

    public static boolean enclosedBy(String startPatter, String endPattern, String path) {
        if (Objects.isNull(startPatter) || Objects.isNull(endPattern) || Objects.isNull(path)) {
            return false;
        }

        return path.startsWith(startPatter) && path.endsWith(endPattern);
    }

    public static List<Integer> doesNotMatchPatterIndexes(String pattern, List<String> paths, int startIndex) {
        return IntStream.range(startIndex, paths.size())
            .boxed()
            .filter(index -> {
                String path = paths.get(index);
                boolean doesNotMatch = PathUtils.doesNotMatchPattern(pattern, path);
                return doesNotMatch;
            })
            .collect(Collectors.toUnmodifiableList());
    }

    public static List<Integer> getIndexesFromStartToEnd(List<String> paths, int startIndex) {
        return IntStream.rangeClosed(startIndex, paths.size() - 1).boxed().collect(Collectors.toUnmodifiableList());
    }

    public static List<Integer> matchPatternIndexes(String pattern, List<String> paths, int startIndex) {
        return IntStream.range(startIndex, paths.size())
            .boxed()
            .filter(index -> {
                String path = paths.get(index);
                boolean match = PathUtils.matchPattern(pattern, path);
                return match;
            })
            .collect(Collectors.toUnmodifiableList());
    }

}
