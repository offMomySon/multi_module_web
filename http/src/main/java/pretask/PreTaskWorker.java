package pretask;

import vo.HttpRequest;
import vo.HttpResponse;

public interface PreTaskWorker {
    boolean execute(HttpRequest httpRequest, HttpResponse httpResponse);
}
