package response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import response.HttpResponseHeader;
import vo.ContentType;

public class HttpResponseHeaderCreator {
    private final SimpleDateFormat simpleDateFormat;
    private final String hostAddress;
    private final ContentType contentType;

    public HttpResponseHeaderCreator(SimpleDateFormat simpleDateFormat, String hostAddress, ContentType optionalContentType) {
        Objects.requireNonNull(simpleDateFormat);
        Objects.requireNonNull(hostAddress);
        Objects.requireNonNull(optionalContentType);
        if (hostAddress.isBlank()) {
            throw new RuntimeException("hostAddress is empty.");
        }

        this.simpleDateFormat = simpleDateFormat;
        this.hostAddress = hostAddress;
        this.contentType = optionalContentType;
    }

    public HttpResponseHeader create() {
        String startLine = "HTTP/1.1 200 OK";

        Map<String, String> header = new HashMap<>();
        header.put("Date", simpleDateFormat.format(new Date()));
        header.put("Host", hostAddress);
        header.put("Content-Type", contentType.getValue());
        header.put("Connection", "close");

        return new HttpResponseHeader(startLine, header);
    }
}