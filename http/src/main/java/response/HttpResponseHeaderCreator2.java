package response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import vo.ContentType;
import vo.ContentType2;

public class HttpResponseHeaderCreator2 {
    private final SimpleDateFormat simpleDateFormat;
    private final String hostAddress;
    private final ContentType2 contentType;

    public HttpResponseHeaderCreator2(SimpleDateFormat simpleDateFormat, String hostAddress, ContentType2 contentType2) {
        Objects.requireNonNull(simpleDateFormat);
        Objects.requireNonNull(hostAddress);
        if (hostAddress.isBlank()) {
            throw new RuntimeException("hostAddress is empty.");
        }

        this.simpleDateFormat = simpleDateFormat;
        this.hostAddress = hostAddress;
        this.contentType = contentType2;
    }

    public HttpResponseHeader create() {
        String startLine = "HTTP/1.1 200 OK";

        Map<String, String> header = new HashMap<>();
        header.put("Date", simpleDateFormat.format(new Date()));
        header.put("Host", hostAddress);
        if(Objects.nonNull(contentType)){
            header.put("Content-Type", contentType.getValue());
        }
        header.put("Connection", "close");

        return new HttpResponseHeader(startLine, header);
    }
}