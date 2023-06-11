package filter.chain;

import vo.HttpRequest;
import vo.HttpResponse;

public interface FilterWorkerChain {

    void execute(HttpRequest request, HttpResponse response);
}
