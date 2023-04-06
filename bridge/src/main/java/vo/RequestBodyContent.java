package vo;

import java.util.Objects;

public class RequestBodyContent {
    private final String value;

    public RequestBodyContent(String value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException("value 가 null 입니다.");
        }

        this.value = value;
    }

    public static RequestBodyContent empty() {
        return new RequestBodyContent("");
    }

    public boolean isEmpty() {
        return Objects.isNull(value) || value.isEmpty() || value.isBlank();
    }

    public String getValue() {
        return value;
    }
}
