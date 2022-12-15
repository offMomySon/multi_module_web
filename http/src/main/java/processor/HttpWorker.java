package processor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import request.HttpRequest;
import response.HttpResponse;
import response.ResponseStatus;
import static io.IoUtils.createBufferedInputStream;
import static io.IoUtils.createBufferedOutputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.validateNull;

/**
 * 역할.
 * socket 의 input 에 대한 비즈니스 로직을 수행하고 output 을 전달하는 역할.
 * 1. http reuqest 를 읽는다..
 * 2. 비스니스 로직을 수행한다.
 * 3. http response 을 전송한다.
 */
@Slf4j
public class HttpWorker implements Runnable {
    private final BufferedInputStream inputStream;
    private final BufferedOutputStream outputStream;

    public HttpWorker(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = createBufferedInputStream(validateNull(inputStream));
        this.outputStream = createBufferedOutputStream(validateNull(outputStream));
    }

    @Override
    public void run() {
        try (inputStream; outputStream) {
            // 1. http request read.
            HttpRequest httpRequest = HttpRequest.parse(inputStream);

            // 3. http response send.
            HttpResponse httpResponse = new HttpResponse(outputStream);
            httpResponse.header(ResponseStatus.OK.getStatusLine())
                .body(new ByteArrayInputStream("test body message\r\n".getBytes(UTF_8)))
                .send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}