package request;

import dto.HttpBody;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import dto.RequestURI;
import dto.Method;
import validate.ValidateUtil;
import static io.IoUtils.creatBufferedReader;
import static validate.ValidateUtil.validateNull;

public class HttpParser {
    private static final String REQUEST_LINE_DELIMITER = " ";

    private final Method method;
    private final RequestURI requestURI;
    private final HttpBody httpBody;

    private HttpParser(Method method, RequestURI requestURI, HttpBody httpBody) {
        this.method = ValidateUtil.validateNull(method);
        this.requestURI = ValidateUtil.validateNull(requestURI);
        this.httpBody = ValidateUtil.validateNull(httpBody);
    }

    public HttpParser parse(InputStream requestInputStream) {
        validateNull(requestInputStream);
        BufferedReader requestBufferedStream = creatBufferedReader(requestInputStream);

        String requestLine;
        String[] requestLineElements;
        try {
            requestLine = requestBufferedStream.readLine();
            requestLineElements = requestLine.split(REQUEST_LINE_DELIMITER, 3);
        } catch (IOException e) {
            throw new RuntimeException("fail to read request buffer.");
        }

        Method method = Method.find(requestLineElements[0]);
        RequestURI requestURI = RequestURI.from(requestLineElements[1]);
        HttpBody httpBody = HttpBody.from(requestInputStream);

        return new HttpParser(method, requestURI, httpBody);
    }
}
