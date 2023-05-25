package vo;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;

public class HttpResponse {
    private final OutputStream outputStream;
    private final Map<String, String> header;

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

    public String getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public HttpResponseSender getSender() {
        return new HttpResponseSender(outputStream, startLine, header);
    }
}
