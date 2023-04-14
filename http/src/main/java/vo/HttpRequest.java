package vo;

import java.io.InputStream;
import java.util.Objects;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final HttpHeader httpHeader;
    private final InputStream requestStream;

    public HttpRequest(HttpMethod httpMethod, HttpUri httpUri, HttpHeader httpHeader, InputStream requestStream) {
        Objects.requireNonNull(httpMethod);
        Objects.requireNonNull(httpUri);
        Objects.requireNonNull(httpHeader);
        Objects.requireNonNull(requestStream);

        this.httpMethod = httpMethod;
        this.httpUri = httpUri;
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

    public InputStream getRequestStream() {
        return requestStream;
    }
}
