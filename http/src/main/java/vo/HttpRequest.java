package vo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import static com.main.util.IoUtils.createBufferedInputStream;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final HttpRequestPath httpRequestPath;
    private final QueryParameters queryParameters;
    private final HttpHeader httpHeader;
    private final InputStream requestStream;

    public HttpRequest(HttpMethod httpMethod, HttpRequestPath httpRequestPath, QueryParameters queryParameters, HttpHeader httpHeader, InputStream requestStream) {
        Objects.requireNonNull(httpMethod);
        Objects.requireNonNull(httpRequestPath);
        Objects.requireNonNull(queryParameters);
        Objects.requireNonNull(httpHeader);
        Objects.requireNonNull(requestStream);

        this.httpMethod = httpMethod;
        this.httpRequestPath = httpRequestPath;
        this.queryParameters = queryParameters;
        this.httpHeader = httpHeader;
        this.requestStream = requestStream;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpRequestPath getHttpRequestPath() {
        return httpRequestPath;
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

    private static String convertToString(InputStream inputStream) {
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
            ", httpUri=" + httpRequestPath +
            ", queryParameters=" + queryParameters +
            ", httpHeader=" + httpHeader +
            ", requestStream=" + requestStream +
            '}';
    }
}
