package request;

import structure.HttpBody;
import structure.HttpHeader;
import structure.Method;
import structure.RequestURI;
import static validate.ValidateUtil.validateNull;

public class UpdateRequest implements Request {
    private final Method method;
    private final RequestURI requestURI;
    private final HttpHeader httpHeader;
    private final HttpBody httpBody;

    public UpdateRequest(Method method, RequestURI requestURI, HttpHeader httpHeader, HttpBody httpBody) {
        this.method = validateNull(method);
        this.requestURI = validateNull(requestURI);
        this.httpHeader = validateNull(httpHeader);
        this.httpBody = validateNull(httpBody);
    }
}
