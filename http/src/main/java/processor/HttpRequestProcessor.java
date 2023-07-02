package processor;

import vo.HttpRequest;
import vo.HttpResponse;

public interface HttpRequestProcessor {
    boolean execute(HttpRequest request, HttpResponse response);
}

