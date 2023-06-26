package matcher.converter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import util.IoUtils;

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

    public static BodyContent from(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        try {
            BufferedInputStream newInputStream = IoUtils.createBufferedInputStream(inputStream);

            String bodyContent = readBodyContent(newInputStream);

            return new BodyContent(bodyContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readBodyContent(BufferedInputStream newInputStream) throws IOException {
        byte[] BUFFER = new byte[8192];
        StringBuilder contentBuilder = new StringBuilder();
        while (newInputStream.available() != 0) {
            int read = newInputStream.read(BUFFER);
            String partOfContent = new String(BUFFER, 0, read);

            contentBuilder.append(partOfContent);
        }
        return contentBuilder.toString();
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
