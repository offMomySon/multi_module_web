package vo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import util.IoUtils;

public class RequestMessageHeaderParser {
    private static final String REQUEST_LINE_DELIMITER = " ";

    private final RequestMessageHeader requestMessageHeader;
    private final InputStream requestStream;

    private RequestMessageHeaderParser(RequestMessageHeader requestMessageHeader, InputStream requestStream) {
        Objects.requireNonNull(requestMessageHeader);
        Objects.requireNonNull(requestStream);

        this.requestMessageHeader = requestMessageHeader;
        this.requestStream = requestStream;
    }

    public static RequestMessageHeaderParser parse(InputStream requestStream) {
        try {
            Objects.requireNonNull(requestStream);

            BufferedReader reader = IoUtils.creatBufferedReader(requestStream);

            String startLine = reader.readLine();
            String[] startLineElement = startLine.split(REQUEST_LINE_DELIMITER, 3);

            HttpMethod httpMethod = HttpMethod.find(startLineElement[0]);
            HttpUri httpUri = HttpUri.from(startLineElement[1]);
            NewHttpHeader httpHeader = createHttpHeader(reader);


            requestStream = combineLeftRequestStream(reader, requestStream);
            RequestMessageHeader requestMessageHeader = new RequestMessageHeader(httpMethod, httpUri, httpHeader);

            return new RequestMessageHeaderParser(requestMessageHeader, requestStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SequenceInputStream combineLeftRequestStream(BufferedReader reader, InputStream requestStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[8192];

        while (reader.ready()) {
            int readLength = reader.read(buffer);
            sb.append(buffer, 0, readLength);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));

        SequenceInputStream sequenceInputStream = new SequenceInputStream(byteArrayInputStream, requestStream);

        return sequenceInputStream;
    }

    private static NewHttpHeader createHttpHeader(BufferedReader reader) throws IOException {
        NewHttpHeader.Builder builder = NewHttpHeader.builder();
        while (reader.ready()) {
            String headerLine = reader.readLine();

            if (headerLine.isEmpty()) {
                break;
            }

            builder.append(headerLine);
        }
        return builder.build();
    }

    public RequestMessageHeader getRequestMessageHeader() {
        return requestMessageHeader;
    }

    public InputStream getRequestStream() {
        return requestStream;
    }

    public static class RequestMessageHeader {
        private final HttpMethod httpMethod;
        private final HttpUri httpUri;
        private final NewHttpHeader httpHeader;

        public RequestMessageHeader(HttpMethod httpMethod, HttpUri httpUri, NewHttpHeader httpHeader) {
            Objects.requireNonNull(httpMethod);
            Objects.requireNonNull(httpUri);
            Objects.requireNonNull(httpHeader);

            this.httpMethod = httpMethod;
            this.httpUri = httpUri;
            this.httpHeader = httpHeader;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public HttpUri getHttpUri() {
            return httpUri;
        }

        public NewHttpHeader getHttpHeader() {
            return httpHeader;
        }
    }
}
