package vo;

import java.io.InputStream;
import java.util.Objects;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final NewHttpHeader httpHeader;
    private final InputStream requestStream;

    public HttpRequest(HttpMethod httpMethod, HttpUri httpUri, NewHttpHeader httpHeader, InputStream requestStream) {
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

    public NewHttpHeader getHttpHeader() {
        return httpHeader;
    }

    public InputStream getRequestStream() {
        return requestStream;
    }
}
