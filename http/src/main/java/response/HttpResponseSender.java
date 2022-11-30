package response;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import static io.IoUtils.createBufferedOutputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validateNull;

public class HttpResponseSender {
    private static final String EMPTY_LINE = "\r\n";

    private final HttpHeaderSender httpHeaderSender;
    private final HttpBodySender httpBodySender;
    private final BufferedOutputStream bufferedOutputStream;

    public HttpResponseSender(HttpHeaderSender httpHeaderSender, OutputStream outputStream, HttpBodySender httpBodySender) {
        this.httpHeaderSender = validateNull(httpHeaderSender);
        this.bufferedOutputStream = createBufferedOutputStream(validateNull(outputStream));
        this.httpBodySender = validateNull(httpBodySender);
    }

    public void send() {
        httpHeaderSender.send();
        sendEmptyLine();
        httpBodySender.send();
    }

    private void sendEmptyLine() {
        try {
            bufferedOutputStream.write(EMPTY_LINE.getBytes(UTF_8));
            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
