package filter;

import java.util.Objects;

public class FilterConfig {
    private final int order;
    private final String urlPattern;

    public FilterConfig(int order, String urlPattern) {
        Objects.requireNonNull(order);
        Objects.requireNonNull(urlPattern);
        this.order = order;
        this.urlPattern = urlPattern;
    }

    public int getOrder() {
        return order;
    }

    public String getUrlPattern() {
        return urlPattern;
    }
}
