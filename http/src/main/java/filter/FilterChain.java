package filter;

import vo.HttpRequest;
import vo.HttpResponse;

public interface FilterChain {
    void doChain(HttpRequest request, HttpResponse response);
}
