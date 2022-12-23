package mapper;

import java.util.Objects;
import lombok.ToString;
import validate.ValidateUtil;
import vo.HttpMethod;

@ToString
public class MethodIndicator {
    private final String httpUri;
    private final HttpMethod httpMethod;

    public MethodIndicator(String httpUri, HttpMethod httpMethod) {
        this.httpUri = ValidateUtil.validateNull(httpUri);
        this.httpMethod = ValidateUtil.validateNull(httpMethod);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodIndicator that = (MethodIndicator) o;
        return Objects.equals(httpUri, that.httpUri) && httpMethod == that.httpMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpUri, httpMethod);
    }
}
