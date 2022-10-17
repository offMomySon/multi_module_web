package request;

import java.text.MessageFormat;
import lombok.Getter;
import lombok.ToString;
import static util.ValidateUtil.*;

@ToString
@Getter
public class RequestLine {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_DELIMITER = "\\?";
    private static final String INIT_INFO = "";
    private static final int REQUEST_PART_SIZE = 3;
    private static final int MAX_REQUEST_URI_SIZE = 2;

    private final Method method;
    private final Uri uri;
    private final String query;
    private final String version;

    private RequestLine(Method method, Uri uri, String query, String version) {
        validateNull(method);
        validateNull(uri);
        validateNull(method);
        validate(query);
        validate(version);

        this.method = method;
        this.uri = uri;
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
        Uri path = Uri.of(requestUriParts[0]);
        String query = INIT_INFO;
        if (requestUriParts.length == 2) {
            query = requestUriParts[1];
        }
        String version = requestLineParts[2];

        return new RequestLine(method, path, query, version);
    }
}
