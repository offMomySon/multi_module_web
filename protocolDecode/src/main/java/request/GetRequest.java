package request;

import structure.HttpHeader;
import structure.RequestURI;
import static validate.ValidateUtil.validateNull;

public class GetRequest implements Request {
    private final RequestURI requestURI;
    private final HttpHeader httpHeader;

    public GetRequest(RequestURI requestURI, HttpHeader httpHeader) {
        this.requestURI = validateNull(requestURI);
        this.httpHeader = validateNull(httpHeader);
    }
}
