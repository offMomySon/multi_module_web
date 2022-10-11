package request;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class FilePath {
    private static final String PREV_FILE_INDICATOR = "..";
    private static final String PATH_SEPARATOR = "/";
    private final String value;

    private FilePath(String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(MessageFormat.format("path is illegalArgument. value = `{0}`", value));
        }
        this.value = value;
    }

    public static FilePath of(String path) {
        String normalizedPath = "";
        try {
            normalizedPath = new URI(path).normalize().getPath();
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageFormat.format("It is a path that cannot be normalized. = `{}`", path), e.getCause());
        }

        List<String> pathChunks = Arrays.stream(normalizedPath.split(PATH_SEPARATOR))
            .filter(p -> !StringUtils.isEmpty(p))
            .filter(p -> !StringUtils.isBlank(p))
            .collect(Collectors.toUnmodifiableList());

        Deque<String> pathStack = new ArrayDeque<>();
        for (String pathChunk : pathChunks) {
            if (StringUtils.equals(PREV_FILE_INDICATOR, pathChunk) && pathStack.isEmpty()) {
                throw new IllegalArgumentException("Do not look under the root path.");
            }
            if (StringUtils.equals(PREV_FILE_INDICATOR, pathChunk)) {
                pathStack.pop();
                continue;
            }

            pathStack.push(pathChunk);
        }

        String actualPath = pathStack.stream().collect(Collectors.joining(PATH_SEPARATOR, PATH_SEPARATOR, ""));
        return new FilePath(actualPath);
    }
}
