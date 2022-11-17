package request;

import structure.HttpHeader;
import structure.Method;
import structure.RequestURI;
import static validate.ValidateUtil.validateNull;

public class ReadRequest implements Request {
    private final Method method;
    private final RequestURI requestURI;
    private final HttpHeader httpHeader;

    public ReadRequest(Method method, RequestURI requestURI, HttpHeader httpHeader) {
        this.method = validateNull(method);
        this.requestURI = validateNull(requestURI);
        this.httpHeader = validateNull(httpHeader);
    }
}
