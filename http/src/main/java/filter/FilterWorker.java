package filter;

import vo.HttpRequest;
import vo.HttpResponse;

public interface FilterWorker {
    boolean prevExecute(HttpRequest httpRequest, HttpResponse httpResponse);

    boolean postExecute(HttpRequest httpRequest, HttpResponse httpResponse);
}
