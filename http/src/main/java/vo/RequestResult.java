package vo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RequestResult {
    private static final String END_OF_LINE = "\r\n";

    private final String statusLine;
    private final Map<String, String> headers;
    private final InputStream resultStream;

    public RequestResult(String statusLine, Map<String, String> headers, InputStream resultStream) {
        if (Objects.isNull(statusLine) || statusLine.isBlank()) {
            throw new RuntimeException("statusLine is empty.");
        }
        if (Objects.isNull(headers) || headers.isEmpty()) {
            throw new RuntimeException("headers is empty.");
        }
        Objects.requireNonNull(resultStream);

        this.statusLine = statusLine;
        this.headers = headers;
        this.resultStream = resultStream;
    }

    public InputStream getInputStream() {
        StringBuilder headerMessageBuilder = new StringBuilder();
        headerMessageBuilder.append(statusLine).append(END_OF_LINE);
        headerMessageBuilder.append(getHeaderMessage()).append(END_OF_LINE);
        headerMessageBuilder.append(END_OF_LINE);

        InputStream headerInputStream = new ByteArrayInputStream(headerMessageBuilder.toString().getBytes(UTF_8));

        return new SequenceInputStream(headerInputStream, resultStream);
    }

    private String getHeaderMessage() {
        return headers.entrySet().stream()
            .map(entry -> MessageFormat.format("{0}: {1}", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(END_OF_LINE));
    }
}
