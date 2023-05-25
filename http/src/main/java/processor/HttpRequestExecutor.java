package processor;

import vo.HttpRequest;
import vo.HttpResponse;

public interface HttpRequestExecutor {
    void execute(HttpRequest request, HttpResponse response);
}

