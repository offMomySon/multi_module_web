package filter.chain;

import vo.HttpRequest;
import vo.HttpResponse;

public interface FilterChain {

    void execute(HttpRequest request, HttpResponse response);
}
