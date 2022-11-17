package request;

import structure.HttpBody;
import structure.HttpHeader;
import structure.RequestURI;
import static validate.ValidateUtil.validateNull;

public class PutRequest implements Request {
    private final RequestURI requestURI;
    private final HttpHeader httpHeader;
    private final HttpBody httpBody;

    public PutRequest(RequestURI requestURI, HttpHeader httpHeader, HttpBody httpBody) {
        this.requestURI = validateNull(requestURI);
        this.httpHeader = validateNull(httpHeader);
        this.httpBody = validateNull(httpBody);
    }
}
