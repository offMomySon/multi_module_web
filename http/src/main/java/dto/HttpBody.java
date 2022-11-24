package dto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static io.IoUtils.createBufferedInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validateNull;

public class HttpBody {
    private final BufferedInputStream bufferedInputStream;

    private HttpBody(BufferedInputStream bufferedInputStream) {
        this.bufferedInputStream = validateNull(bufferedInputStream);
    }

    public static HttpBody from(InputStream inputStream) {
        validateNull(inputStream);

        if (isEmptyBody(inputStream)) {
            return generateEmptyBody();
        }

        BufferedInputStream bufferedInputStream = createBufferedInputStream(inputStream);
        return new HttpBody(bufferedInputStream);
    }

    private static boolean isEmptyBody(InputStream inputStream) {
        try {
            return inputStream.available() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpBody generateEmptyBody() {
        String emptyMessage = "";

        InputStream inputStream = new ByteArrayInputStream(emptyMessage.getBytes(UTF_8));
        BufferedInputStream bufferedInputStream = createBufferedInputStream(inputStream);
        return new HttpBody(bufferedInputStream);
    }

    public byte[] read() {
        try {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
