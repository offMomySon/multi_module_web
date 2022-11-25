package response;

import java.io.BufferedOutputStream;
import java.io.IOException;
import static validate.ValidateUtil.validateNull;

public class HttpResponser {
    private final HttpResponseMessageCreator httpResponseMessageCreator;
    private final BufferedOutputStream bufferedOutputStream;

    public HttpResponser(HttpResponseMessageCreator httpResponseMessageCreator, BufferedOutputStream bufferedOutputStream) {
        this.httpResponseMessageCreator = validateNull(httpResponseMessageCreator);
        this.bufferedOutputStream = validateNull(bufferedOutputStream);
    }

    public void send() {
        doSendHeader();
        doSendSeparator();
        doSendBody();
    }

    private void doSendHeader() {
        byte[] header = httpResponseMessageCreator.generateHeader();
        try {
            bufferedOutputStream.write(header);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSendSeparator() {
        byte[] separator = httpResponseMessageCreator.generateSeparator();
        try {
            bufferedOutputStream.write(separator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSendBody() {
        try {
            while (httpResponseMessageCreator.isLeftContent()) {
                byte[] content = httpResponseMessageCreator.generateContent();
                bufferedOutputStream.write(content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
