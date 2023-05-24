package vo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import static util.IoUtils.createBufferedInputStream;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final QueryParameters queryParameters;
    private final HttpHeader httpHeader;
    private final InputStream requestStream;

    public HttpRequest(HttpMethod httpMethod, HttpUri httpUri, QueryParameters queryParameters, HttpHeader httpHeader, InputStream requestStream) {
        Objects.requireNonNull(httpMethod);
        Objects.requireNonNull(httpUri);
        Objects.requireNonNull(queryParameters);
        Objects.requireNonNull(httpHeader);
        Objects.requireNonNull(requestStream);

        this.httpMethod = httpMethod;
        this.httpUri = httpUri;
        this.queryParameters = queryParameters;
        this.httpHeader = httpHeader;
        this.requestStream = requestStream;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpUri getHttpUri() {
        return httpUri;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public InputStream getBodyInputStream() {
        return requestStream;
    }

    public String getBodyString() {
        return convertToString(requestStream);
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }

    public static String convertToString(InputStream inputStream) {
        Objects.requireNonNull(inputStream);

        try {
            inputStream = createBufferedInputStream(inputStream);
            byte[] BUFFER = new byte[8192];

            StringBuilder contentBuilder = new StringBuilder();
            while (inputStream.available() != 0) {
                int read = inputStream.read(BUFFER);
                String partOfContent = new String(BUFFER, 0, read);
                contentBuilder.append(partOfContent);
            }
            return contentBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
            "httpMethod=" + httpMethod +
            ", httpUri=" + httpUri +
            ", queryParameters=" + queryParameters +
            ", httpHeader=" + httpHeader +
            ", requestStream=" + requestStream +
            '}';
    }
}
