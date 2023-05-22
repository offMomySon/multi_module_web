package filter;

import vo.HttpRequestReader;
import vo.HttpResponseSender;

public interface FilterChain {
    void doChain(HttpRequestReader requestReader, HttpResponseSender httpResponseSender);
}
