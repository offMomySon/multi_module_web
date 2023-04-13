package processor;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import util.IoUtils;
import vo.HttpMethod;
import vo.HttpUri;
import vo.NewHttpHeader;
import vo.RequestMessageHeaderParser;
import vo.RequestMessageHeaderParser.RequestMessageHeader;

public class NewWorker implements Runnable {
    private final byte[] BUFFER = new byte[8192];

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final HttpRequestExecutor httpRequestExecutor;

    public NewWorker(InputStream inputStream, OutputStream outputStream, HttpRequestExecutor httpRequestExecutor) {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(outputStream);
        Objects.requireNonNull(httpRequestExecutor);

        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void run() {
        RequestMessageHeaderParser messageHeaderParser = RequestMessageHeaderParser.parse(inputStream);
        RequestMessageHeader requestMessageHeader = messageHeaderParser.getRequestMessageHeader();

        HttpMethod httpMethod = requestMessageHeader.getHttpMethod();
        HttpUri httpUri = requestMessageHeader.getHttpUri();
        NewHttpHeader httpHeader = requestMessageHeader.getHttpHeader();
        InputStream requestStream = messageHeaderParser.getRequestStream();

        Object result = httpRequestExecutor.execute(httpMethod, httpUri, httpHeader, requestStream, outputStream);

        sendResponse(result);

        closeStream();
    }

    private void closeStream() {
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(Object result) {
        try {
            InputStream resultInputStream = new ByteArrayInputStream(result.toString().getBytes(StandardCharsets.UTF_8));
            resultInputStream = IoUtils.createBufferedInputStream(resultInputStream);

            BufferedOutputStream bufferedOutputStream = IoUtils.createBufferedOutputStream(outputStream);
            while (resultInputStream.available() != 0) {
                int read = resultInputStream.read(BUFFER);
                bufferedOutputStream.write(BUFFER, 0, read);
            }

            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
