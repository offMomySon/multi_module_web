package filter.chain;

import vo.HttpRequest;
import vo.HttpResponse;

public interface FilterChain {

    boolean execute(HttpRequest request, HttpResponse response);
}
