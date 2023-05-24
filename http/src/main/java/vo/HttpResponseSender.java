package vo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.IoUtils.createBufferedInputStream;
import static util.IoUtils.createBufferedOutputStream;

public class HttpResponseSender implements Closeable {
    private static final String END_OF_LINE = "\r\n";

    private final BufferedOutputStream outputStream;

    private final String startLine;
    private final Map<String, String> header;

    public HttpResponseSender(OutputStream outputStream, String startLine, Map<String, String> header) {
        Objects.requireNonNull(outputStream);
        Objects.requireNonNull(startLine);
        Objects.requireNonNull(header);

        this.outputStream = createBufferedOutputStream(outputStream);
        this.startLine = startLine;
        this.header = header.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public void send(InputStream content) {
        Objects.requireNonNull(content);

        InputStream header = createHeader();
        SequenceInputStream response = new SequenceInputStream(header, content);

        doSend(response);
    }

    public void send(String content) {
        Objects.requireNonNull(content);

        InputStream header = createHeader();
        ByteArrayInputStream body = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        SequenceInputStream response = new SequenceInputStream(header, body);

        doSend(response);
    }

    private InputStream createHeader() {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(startLine).append(END_OF_LINE);
        headerBuilder.append(createHeaderMessage()).append(END_OF_LINE);
        headerBuilder.append(END_OF_LINE);
        return new ByteArrayInputStream(headerBuilder.toString().getBytes(UTF_8));
    }

    private String createHeaderMessage() {
        return header.entrySet().stream()
            .map(entry -> MessageFormat.format("{0}: {1}", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(END_OF_LINE));
    }

    private void doSend(InputStream inputStream) {
        Objects.requireNonNull(inputStream);

        BufferedInputStream newBodyStream = createBufferedInputStream(inputStream);
        byte[] BUFFER = new byte[8192];

        try (newBodyStream) {
            int bytesRead;
            while ((bytesRead = newBodyStream.read(BUFFER)) != -1) {
                outputStream.write(BUFFER, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
