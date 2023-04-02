package vo;

import java.util.Objects;

public class RequestBody {
    private final String value;

    public RequestBody(String value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException("value 가 null 입니다.");
        }

        this.value = value;
    }

    public static RequestBody empty() {
        return new RequestBody("");
    }
}
