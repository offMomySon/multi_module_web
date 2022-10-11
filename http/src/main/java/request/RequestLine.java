package request;

import java.text.MessageFormat;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
@Getter
public class RequestLine {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_DELIMITER = "\\?";
    private static final String INIT_INFO = "";
    private static final int REQUEST_PART_SIZE = 3;
    private static final int MAX_REQUEST_URI_SIZE = 2;

    private final Method method;
    private final FilePath filePath;
    private final String query;
    private final String version;

    private RequestLine(Method method, FilePath filePath, String query, String version) {
        if (Objects.isNull(method)) {
            throw new IllegalArgumentException(MessageFormat.format("method is wrong value : `{}`", method));
        }
        if (Objects.isNull(filePath)) {
            throw new IllegalArgumentException(MessageFormat.format("filePath is wrong value : `{}`", filePath));
        }
        if (Objects.isNull(query)) {
            throw new IllegalArgumentException(MessageFormat.format("query is wrong value : `{}`", query));
        }
        if (StringUtils.isEmpty(version) || StringUtils.isBlank(version)) {
            throw new IllegalArgumentException(MessageFormat.format("version is wrong value : `{}`", version));
        }

        this.method = method;
        this.filePath = filePath;
        this.query = query;
        this.version = version;
    }

    public static RequestLine of(String requestLine) {
        String[] requestLineParts = requestLine.split(REQUEST_LINE_DELIMITER, -1);
        boolean doesNotSatisfyRequestPartSize = requestLineParts.length != REQUEST_PART_SIZE;
        if (doesNotSatisfyRequestPartSize) {
            throw new IllegalArgumentException(
                MessageFormat.format("request line need part of 3. divided part = `{}`", requestLineParts.length));
        }

        String requestUri = requestLineParts[1];
        String[] requestUriParts = requestUri.split(QUERY_DELIMITER, -1);
        boolean doesNotSatisfyRequestUriSize = requestUriParts.length > MAX_REQUEST_URI_SIZE;
        if (doesNotSatisfyRequestUriSize) {
            throw new IllegalArgumentException(
                MessageFormat.format("requestUris need need less part of 3. divided part = `{}`", requestUriParts.length));
        }

        Method method = Method.find(requestLineParts[0])
            .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("일치하는 method 가 존재하지 않습니다. method = `{}`", requestLineParts[0])));
        FilePath path = FilePath.of(requestUriParts[0]);
        String query = INIT_INFO;
        if (requestUriParts.length == 2) {
            query = requestUriParts[1];
        }
        String version = requestLineParts[2];

        return new RequestLine(method, path, query, version);
    }
}
