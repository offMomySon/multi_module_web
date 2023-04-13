package processor;

import java.io.InputStream;
import java.io.OutputStream;
import vo.HttpMethod;
import vo.HttpUri;
import vo.NewHttpHeader;

public interface HttpRequestExecutor {
    Object execute(HttpMethod httpMethod, HttpUri httpUri, NewHttpHeader httpHeader, InputStream inputStream, OutputStream outputStream);
}

