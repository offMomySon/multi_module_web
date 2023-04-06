package processor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import vo.HttpHeader;
import vo.HttpResponse;
import vo.HttpStatus;
import vo.HttpUri;
import vo.RequestMethod;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.IoUtils.creatBufferedReader;
import static util.IoUtils.createBufferedInputStream;
import static util.IoUtils.createBufferedOutputStream;

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

    private final BufferedOutputStream responseStream;

    private final RequestMethod requestMethod;
    private final HttpUri httpUri;
    private final HttpHeader httpHeader;
    private final BufferedInputStream requestStream;

    private HttpWorker(OutputStream responseStream, RequestMethod requestMethod, HttpUri httpUri, HttpHeader httpHeader, InputStream requestStream) {
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(httpUri);
        Objects.requireNonNull(httpHeader);
        Objects.requireNonNull(requestStream);

        this.responseStream = createBufferedOutputStream(responseStream);
        this.requestMethod = requestMethod;
        this.httpUri = httpUri;
        this.httpHeader = httpHeader;
        this.requestStream = createBufferedInputStream(requestStream);
    }

    public void run() {
        try (responseStream; requestStream) {
            // 3. http response send.
            HttpResponse httpResponse = HttpResponse.builder().httpVersion("HTTP/1.1")
                .status(HttpStatus.OK.getCode())
                .statusMessage(HttpStatus.OK.getMessage())
                .httpHeader(HttpHeader.builder().build())
                .sourceInputStream(new ByteArrayInputStream("test body message".getBytes(UTF_8)))
                .responseOutputStream(this.responseStream)
                .build();

            httpResponse.send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpWorker create(InputStream requestStream, OutputStream responseStream) {
        try {
            Objects.requireNonNull(requestStream);
            Objects.requireNonNull(requestStream);

            BufferedReader requestReader = creatBufferedReader(requestStream);

            String startLine = requestReader.readLine();
            String[] startLineElement = startLine.split(REQUEST_LINE_DELIMITER, 3);

            RequestMethod requestMethod = RequestMethod.find(startLineElement[0]);
            HttpUri httpUri = HttpUri.from(startLineElement[1]);

            HttpHeader.Builder httpBuilder = HttpHeader.builder();
            while (true) {
                String headerLine = requestReader.readLine();

                if (isEndOfHeader(headerLine)) {
                    break;
                }

                httpBuilder.append(headerLine);
            }
            HttpHeader httpHeader = httpBuilder.build();

            InputStream combinedRequestInputStream = combineInputStream(requestStream, requestReader);

            return new HttpWorker(responseStream, requestMethod, httpUri, httpHeader, combinedRequestInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SequenceInputStream combineInputStream(InputStream requestStream, BufferedReader requestReader) throws IOException {
        StringBuilder streamCollector = new StringBuilder();
        char[] buffer = new char[8192];

        while (requestReader.ready()) {
            int readLength = requestReader.read(buffer);
            streamCollector.append(buffer, 0, readLength);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(streamCollector.toString().getBytes(UTF_8));

        return new SequenceInputStream(byteArrayInputStream, requestStream);
    }

    private static boolean isEndOfHeader(String headerLine) {
        return headerLine.isEmpty();
    }
}