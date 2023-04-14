package vo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Objects;
import util.IoUtils;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpRequestReader implements Closeable {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_PARAM_STARTER = "?";

    private final InputStream requestStream;

    public HttpRequestReader(InputStream requestStream) {
        Objects.requireNonNull(requestStream);
        this.requestStream = requestStream;
    }

    public HttpRequest read() {
        try {
            Objects.requireNonNull(requestStream);

            BufferedReader reader = IoUtils.creatBufferedReader(requestStream);

            String startLine = reader.readLine();
            String[] startLineElements = startLine.split(REQUEST_LINE_DELIMITER, 3);
            String[] uriElements = startLineElements[1].split(QUERY_PARAM_STARTER, 2);

            HttpMethod httpMethod = HttpMethod.find(startLineElements[0]);
            HttpUri httpUri = HttpUri.from(uriElements[0]);
            QueryParameters queryParameters = QueryParameters.from(uriElements[1]);
            HttpHeader httpHeader = createHttpHeader(reader);
            InputStream newRequestStream = combineRequestStream(reader, requestStream);

            return new HttpRequest(httpMethod, httpUri, queryParameters, httpHeader, newRequestStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SequenceInputStream combineRequestStream(BufferedReader reader, InputStream requestStream) throws IOException {
        char[] buffer = new char[8192];

        StringBuilder headerCollector = new StringBuilder();
        while (reader.ready()) {
            int readLength = reader.read(buffer);
            headerCollector.append(buffer, 0, readLength);
        }
        byte[] headerBytes = headerCollector.toString().getBytes(UTF_8);

        ByteArrayInputStream headerInputStream = new ByteArrayInputStream(headerBytes);

        return new SequenceInputStream(headerInputStream, requestStream);
    }

    private static HttpHeader createHttpHeader(BufferedReader reader) throws IOException {
        HttpHeader.Builder httpHeaderBuilder = HttpHeader.builder();
        while (reader.ready()) {
            String headerLine = reader.readLine();
            if (headerLine.isEmpty()) {
                break;
            }
            httpHeaderBuilder.append(headerLine);
        }
        return httpHeaderBuilder.build();
    }

    @Override
    public void close() throws IOException {
        requestStream.close();
    }
}
