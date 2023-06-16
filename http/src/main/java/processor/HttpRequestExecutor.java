package processor;

import vo.HttpRequest;
import vo.HttpResponse;

public interface HttpRequestExecutor {
    boolean execute(HttpRequest request, HttpResponse response);
}

