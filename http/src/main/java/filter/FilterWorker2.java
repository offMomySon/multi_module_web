package filter;

import vo.HttpRequest;
import vo.HttpResponse;

public interface FilterWorker2 {
    void prevExecute(HttpRequest httpRequest, HttpResponse httpResponse);

    void postExecute(HttpRequest httpRequest, HttpResponse httpResponse);
}
