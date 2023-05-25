package filter;

import vo.HttpRequest;
import vo.HttpResponse;

public interface Filter {

    boolean isPossibleFilterPattern(String pathUrl);

    void doChain(HttpRequest request, HttpResponse response, FilterChain chain);
}
