package processor;

import vo.HttpRequest;
import vo.RequestResult;

public interface HttpRequestExecutor {
    RequestResult execute(HttpRequest httpRequest);
}

