package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class IoUtils {
    private static final int BUFFER_SIZE = 8192;

    public static BufferedReader creatBufferedReader(InputStream inputStream) {
        Objects.requireNonNull(inputStream);

        return new BufferedReader(new InputStreamReader(createBufferedInputStream(inputStream), StandardCharsets.UTF_8), BUFFER_SIZE);
    }

    public static BufferedWriter createBufferedWriter(OutputStream outputStream) {
        Objects.requireNonNull(outputStream);

        return new BufferedWriter(new OutputStreamWriter(createBufferedOutputStream(outputStream), StandardCharsets.UTF_8), BUFFER_SIZE);
    }

    public static BufferedInputStream createBufferedInputStream(InputStream inputStream) {
        Objects.requireNonNull(inputStream);

        return new BufferedInputStream(inputStream, BUFFER_SIZE);
    }

    public static BufferedOutputStream createBufferedOutputStream(OutputStream outputStream) {
        Objects.requireNonNull(outputStream);

        return new BufferedOutputStream(outputStream, BUFFER_SIZE);
    }
}
