package vo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;

public class HttpResponse {
    private final OutputStream outputStream;

    private String startline;
    private Map<String, String> header;
    private InputStream inputStream;

    public HttpResponse(OutputStream outputStream) {
        if (ObjectUtils.isEmpty(outputStream)) {
            throw new RuntimeException("outputStream is empty.");
        }
        this.outputStream = outputStream;
    }

    public void setStartline(String startline) {
        this.startline = startline;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getStartline() {
        return startline;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public InputStream getInputStream() {
        return inputStream;
    }


}
