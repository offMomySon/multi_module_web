package filter;

import vo.HttpRequest;
import vo.HttpResponse;

public interface Filter {
    void doChain(HttpRequest request, HttpResponse response, FilterChain chain);
}
