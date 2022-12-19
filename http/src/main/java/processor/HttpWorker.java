package processor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import request.Method;
import request.RequestURI;
import response.HttpResponse;
import response.ResponseStatus;
import static io.IoUtils.creatBufferedReader;
import static io.IoUtils.createBufferedInputStream;
import static io.IoUtils.createBufferedOutputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validateNull;

/**
 * 역할.
 * socket 의 input 에 대한 비즈니스 로직을 수행하고 output 을 전달하는 역할.
 * 1. http reuqest 를 읽는다..
 * 2. 비스니스 로직을 수행한다.
 * 3. http response 을 전송한다.
 */
@Slf4j
public class HttpWorker implements Runnable {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String HEADER_VALUE_DELIMITER = ",";

    private final BufferedOutputStream responseStream;

    private final Method method;
    private final RequestURI requestURI;
    private final Map<String, Set<String>> header;
    private final BufferedInputStream requestStream;

    public static HttpWorker create(InputStream requestStream, OutputStream responseStream) {
        try {
            validateNull(requestStream);
            validateNull(responseStream);

            BufferedReader requestReader = creatBufferedReader(requestStream);

            String startLine = requestReader.readLine();
            String[] startLineElement = startLine.split(REQUEST_LINE_DELIMITER, 3);

            Method method = Method.find(startLineElement[0]);
            RequestURI requestURI = RequestURI.from(startLineElement[1]);
            Map<String, Set<String>> header = generateHeader(requestReader);

            InputStream combinedRequestInputStream = combineInputStream(requestStream, requestReader);

            return new HttpWorker(responseStream, method, requestURI, header, combinedRequestInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SequenceInputStream combineInputStream(InputStream requestStream, BufferedReader requestReader) throws IOException {
        StringBuilder streamCollector = new StringBuilder();
        char[] buffer = new char[8192];

        while(requestReader.ready()){
            int readLength = requestReader.read(buffer);
            streamCollector.append(buffer, 0, readLength);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(streamCollector.toString().getBytes(UTF_8));

        return new SequenceInputStream(byteArrayInputStream, requestStream);
    }

    private static Map<String, Set<String>> generateHeader(BufferedReader requestReader) throws IOException {
        Map<String, Set<String>> header = new HashMap<>();
        while (true) {
            String headerLine = requestReader.readLine();

            if (isEndOfHeader(headerLine)) {
                break;
            }

            String[] splitHeader = headerLine.split(HEADER_KEY_VALUE_DELIMITER, 2);

            String headerKey = splitHeader[0];
            Set<String> headerValue = Arrays.stream(splitHeader[1].split(HEADER_VALUE_DELIMITER)).map(String::trim).collect(Collectors.toUnmodifiableSet());

            header.put(headerKey, headerValue);
        }
        return header;
    }

    private static boolean isEndOfHeader(String headerLine) {
        return headerLine.isEmpty();
    }

    public HttpWorker(OutputStream responseStream, Method method, RequestURI requestURI, Map<String, Set<String>> header, InputStream requestStream) {
        this.responseStream = createBufferedOutputStream(validateNull(responseStream));

        this.method = validateNull(method);
        this.requestURI = validateNull(requestURI);
        this.header = createNewHeader(validateNull(header));
        this.requestStream = createBufferedInputStream(validateNull(requestStream));
    }

    private static Map<String, Set<String>> createNewHeader(Map<String, Set<String>> header) {
        return header.entrySet().stream()
            .filter(e -> Objects.nonNull(e.getKey()))
            .map(HttpWorker::createEntryMapExcludeNullValue)
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, Set<String>> createEntryMapExcludeNullValue(Map.Entry<String, Set<String>> e) {
        String key = e.getKey();
        Set<String> values = e.getValue().stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());

        return Map.entry(key, values);
    }

    public void run() {
        try (responseStream; requestStream) {
            // 3. http response send.
            HttpResponse httpResponse = new HttpResponse(responseStream);
            httpResponse.header(ResponseStatus.OK.getStatusLine())
                .body(new ByteArrayInputStream("test body message\r\n".getBytes(UTF_8)))
                .send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}