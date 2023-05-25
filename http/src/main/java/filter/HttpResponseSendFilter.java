package filter;

import java.io.IOException;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;

public class HttpResponseSendFilter implements Filter {

    public HttpResponseSendFilter() {
    }

    @Override
    public boolean isPossibleFilterPattern(String pathUrl) {
        return true;
    }

    @Override
    public void doChain(HttpRequest request, HttpResponse response, FilterChain chain) {
        chain.doChain(request, response);

        if (response.isClosed()) {
            return;
        }

        try {
            HttpResponseWriter sender = response.getSender();
            sender.flush();
            sender.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
