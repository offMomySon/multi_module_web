package filter;

import vo.HttpRequestReader;
import vo.HttpResponseSender;

public interface Filter {
    void doChain(HttpRequestReader requestReader, HttpResponseSender httpResponseSender, FilterChain chain);
}
