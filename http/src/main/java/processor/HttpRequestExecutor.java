package processor;

import java.io.InputStream;
import vo.HttpMethod;
import vo.HttpUri;
import vo.NewHttpHeader;
import vo.RequestResult;

public interface HttpRequestExecutor {
    RequestResult execute(HttpMethod httpMethod, HttpUri httpUri, NewHttpHeader httpHeader, InputStream inputStream);
}

