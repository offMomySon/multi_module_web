package processor;

import java.io.InputStream;
import vo.HttpHeader;
import vo.HttpMethod;
import vo.HttpUri;
import vo.RequestResult;

public interface HttpRequestExecutor {
    RequestResult execute(HttpMethod httpMethod, HttpUri httpUri, HttpHeader httpHeader, InputStream inputStream);
}

