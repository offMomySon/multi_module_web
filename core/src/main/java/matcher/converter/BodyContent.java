package matcher.converter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import static com.main.util.IoUtils.createBufferedInputStream;

@Slf4j
public class BodyContent {
    private final String value;

    public BodyContent(String value) {
        if (Objects.isNull(value)) {
            this.value = "";
            return;
        }
        this.value = value;
    }

    public static BodyContent from(InputStream bodyInputStream) {
        Objects.requireNonNull(bodyInputStream);
        BufferedInputStream newBodyInputStream = createBufferedInputStream(bodyInputStream);

        String body = readBody(newBodyInputStream);
        return new BodyContent(body);
    }

    private static String readBody(BufferedInputStream bufferedInputStream) {
        byte[] readAllBytes = readAllBytes(bufferedInputStream);
        return new String(readAllBytes);
    }

    private static byte[] readAllBytes(BufferedInputStream bufferedInputStream) {
        try {
            return bufferedInputStream.readAllBytes();
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
