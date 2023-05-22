package processor;

import vo.HttpRequestReader;
import vo.HttpResponseSender;
import vo.RequestResult;

public interface HttpRequestExecutor {
    RequestResult execute(HttpRequestReader httpRequest, HttpResponseSender httpResponseSender);
}

