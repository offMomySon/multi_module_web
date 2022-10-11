package request;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class HttpRequest {
    private static final String HEADER_END_LINE = "\r\n\r\n";
    private static final String END_OF_LINE = "\r\n";
    private static final String HEADER_LINE_DELIMITER = ":";
    private static final String HEADER_VALUE_DELIMITER = ",";
    private static final String INIT_BODY = "";

    private final RequestLine requestLine;
    private final Map<String, Set<String>> headers;
    private final String body;

    public FilePath getPath() {
        return requestLine.getFilePath();
    }

    public String getQueryString() {
        return requestLine.getQuery();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public Set<String> getHeaderKeys(){
        return headers.keySet().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getHeaderValue(String key){
        return headers.get(key).stream().collect(Collectors.toUnmodifiableSet());
    }

    public String getBody() {
        return body;
    }

    private HttpRequest(RequestLine requestLine, Map<String, Set<String>> headers, String body) {
        if(Objects.isNull(requestLine)){
            throw new IllegalArgumentException(MessageFormat.format("httpRequestLine is wrong value : {}", requestLine));
        }
        if (Objects.isNull(headers) || headers.isEmpty()) {
            throw new IllegalArgumentException(MessageFormat.format("headers is wrong value : {}", headers));
        }
        if (Objects.isNull(body)) {
            throw new IllegalArgumentException(MessageFormat.format("body is wrong value : {}", body));
        }

        Map<String, Set<String>> newHeader = headers.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        this.requestLine = requestLine;
        this.headers = newHeader;
        this.body = body;
    }

    public static HttpRequest of(String request) {
        String[] requestParts = request.split(HEADER_END_LINE, 2);

        String[] headerLines = requestParts[0].split(END_OF_LINE);
        String[] requestHeaders = Arrays.stream(headerLines, 1, headerLines.length).toArray(String[]::new);

        RequestLine requestLine = RequestLine.of(headerLines[0]);
        Map<String, Set<String>> header = parseHeader(requestHeaders);
        String body = INIT_BODY;
        if (requestParts.length == 2) {
            body = requestParts[1];
        }

        return new HttpRequest(requestLine, header, body);
    }

    /**
     *  1. general header
     *  2. request header
     *  3. entity header
     */
    private static HashMap<String, Set<String>> parseHeader(String[] requestHeaders) {
        HashMap<String, Set<String>> header = new HashMap<>();

        for (String requestHeader : requestHeaders) {
            String[] splitRequestHeader = requestHeader.split(HEADER_LINE_DELIMITER, 2);
            String key = splitRequestHeader[0];
            String valueElements = splitRequestHeader[1];

            Set<String> values = Arrays.stream(valueElements.split(HEADER_VALUE_DELIMITER))
                .map(String::trim)
                .collect(Collectors.toUnmodifiableSet());

            header.put(key, values);
        }

        return header;
    }
}
