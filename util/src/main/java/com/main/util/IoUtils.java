package com.main.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class IoUtils {
    private static final int BUFFER_SIZE = 8192;

    public static BufferedReader createBufferedReader(InputStream inputStream) {
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
