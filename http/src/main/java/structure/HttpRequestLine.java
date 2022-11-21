package structure;

import static validate.ValidateUtil.validateNull;

public class HttpRequestLine {
    private static final String DELIMITER = " ";

    private final Method method;
    private final RequestURI requestURI;

    public HttpRequestLine(Method method, RequestURI requestURI) {
        this.method = validateNull(method);
        this.requestURI = validateNull(requestURI);
    }

    public static HttpRequestLine from(String requestLine) {
        String[] requestLines = requestLine.split(DELIMITER);

        Method method = Method.find(requestLines[0]);
        RequestURI requestURI = RequestURI.from(requestLines[1]);

        return new HttpRequestLine(method, requestURI);
    }
}
