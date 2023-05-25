package vo;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.IoUtils.createBufferedInputStream;

public class HttpResponse implements Closeable {
    private static final String END_OF_LINE = "\r\n";

    private final OutputStream outputStream;
    private final Map<String, String> header;

    private boolean hasHeaderSent = false;
    private boolean isClosed = false;

    private String startLine;

    public HttpResponse(OutputStream outputStream) {
        if (ObjectUtils.isEmpty(outputStream)) {
            throw new RuntimeException("outputStream is empty.");
        }
        this.outputStream = outputStream;
        this.header = new HashMap<>();
    }

    public void setStartLine(String startLine) {
        Objects.requireNonNull(startLine);
        this.startLine = startLine;
    }

    public void appendHeader(String key, String value) {
        this.header.put(key, value);
    }

    public void appendHeader(Map<String, String> otherHeader) {
        Objects.requireNonNull(otherHeader);

        otherHeader.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .forEach(entry -> header.put(entry.getKey(), entry.getValue()));
    }

    // todo
    // outputstream 이 close 체크를 할 텐데
    // httpResponse 도 close 체크할 필요가 있을까?
    // outputstream 의 역할을 굳이 httpResponse 단에서 가져와 처리할 필요가 있을까?
    public void send(InputStream body) {
        if (isClosed) {
            throw new RuntimeException("socket is close.");
        }

        Objects.requireNonNull(body);

        if (!hasHeaderSent) {
            hasHeaderSent = true;

            InputStream header = createHeader();
            doSend(header);
        }

        doSend(body);
    }

    public void flush() {
        if (isClosed) {
            throw new RuntimeException("socket is close.");
        }

        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSend(InputStream content) {
        BufferedInputStream contentStream = createBufferedInputStream(content);
        byte[] BUFFER = new byte[8192];

        try (contentStream) {
            int readBytes;
            while ((readBytes = contentStream.read(BUFFER)) != -1) {
                outputStream.write(BUFFER, 0, readBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public HttpResponseWriter getSender() {
        return new HttpResponseWriter(this);
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean doesNotClosed() {
        return !isClosed();
    }

    @Override
    public void close() throws IOException {
        flush();

        isClosed = true;
        outputStream.close();
    }
}
