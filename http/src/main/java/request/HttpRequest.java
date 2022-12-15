package request;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import static io.IoUtils.creatBufferedReader;
import static io.IoUtils.createBufferedInputStream;
import static validate.ValidateUtil.validateNull;

@Getter
public class HttpRequest {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String HEADER_VALUE_DELIMITER = ",";
    private static final String END_OF_LINE = "\r\n";

    private final Method method;
    private final RequestURI requestURI;
    private final Map<String, Set<String>> header;
    private final BufferedInputStream bodyInputStream;

    private HttpRequest(Method method, RequestURI requestURI, Map<String, Set<String>> header, InputStream inputStream) {
        Map<String, Set<String>> newHeader = validateNull(header).entrySet().stream()
            .map(e -> Map.entry(e.getKey(), e.getValue().stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet())))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        this.method = validateNull(method);
        this.requestURI = validateNull(requestURI);
        this.header = newHeader;
        this.bodyInputStream = createBufferedInputStream(validateNull(inputStream));
    }

    public static HttpRequest parse(InputStream requestInputStream) {
        validateNull(requestInputStream);
        BufferedReader reader = creatBufferedReader(requestInputStream);

        String[] requestLineElements;
        try {
            String requestLine = reader.readLine();
            requestLineElements = requestLine.split(REQUEST_LINE_DELIMITER, 3);
        } catch (IOException e) {
            throw new RuntimeException("fail to read request buffer.");
        }

        Method method = Method.find(requestLineElements[0]);
        RequestURI requestURI = RequestURI.from(requestLineElements[1]);
        Map<String, Set<String>> header = parseHeader(reader);

        return new HttpRequest(method, requestURI, header, requestInputStream);
    }

    private static Map<String, Set<String>> parseHeader(BufferedReader reader) {
        Map<String, Set<String>> header = new HashMap<>();
        try {
            while (true) {
                String headerLine = reader.readLine();

                if (isEndOfHeader(headerLine)) {
                    break;
                }

                String[] splitHeaderLine = headerLine.split(HEADER_KEY_VALUE_DELIMITER, 2);

                String key = splitHeaderLine[0].trim();
                Set<String> values = Arrays.stream(splitHeaderLine[1].split(HEADER_VALUE_DELIMITER)).map(String::trim).collect(Collectors.toUnmodifiableSet());

                header.put(key, values);
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to read request buffer.");
        }

        return header;
    }

    private static boolean isEndOfHeader(String headerLine) {
        return headerLine.isEmpty();
    }
}
