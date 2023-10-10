package task;

import vo.HttpRequest;
import vo.HttpResponse;

public interface PostTaskWorker {
    boolean execute(HttpRequest httpRequest, HttpResponse httpResponse);
}
