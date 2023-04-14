package vo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import util.IoUtils;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BodyContent {
    private final String value;

    public BodyContent(String value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException("value 가 null 입니다.");
        }

        this.value = value;
    }

    public static BodyContent from(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        BufferedInputStream bufferedInputStream = IoUtils.createBufferedInputStream(inputStream);

        try {
            byte[] bytes = bufferedInputStream.readAllBytes();
            String value = new String(bytes, UTF_8);

            return new BodyContent(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BodyContent empty() {
        return new BodyContent("");
    }

    public boolean isEmpty() {
        return Objects.isNull(value) || value.isEmpty() || value.isBlank();
    }

    public String getValue() {
        return value;
    }
}
