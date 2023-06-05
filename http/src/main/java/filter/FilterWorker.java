package filter;

import vo.HttpRequest;
import vo.HttpResponse;

public interface FilterWorker {

    void doChain(HttpRequest request, HttpResponse response, FilterChain chain);
}
