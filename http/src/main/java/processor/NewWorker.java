package processor;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import util.IoUtils;
import vo.HttpMethod;
import vo.HttpUri;
import vo.NewHttpHeader;
import vo.RequestMessageHeaderParser;
import vo.RequestResult;

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

        HttpMethod httpMethod = messageHeaderParser.getHttpMethod();
        HttpUri httpUri = messageHeaderParser.getHttpUri();
        NewHttpHeader httpHeader = messageHeaderParser.getHttpHeader();
        InputStream requestStream = messageHeaderParser.getRequestStream();

        RequestResult result = httpRequestExecutor.execute(httpMethod, httpUri, httpHeader, requestStream, outputStream);

        sendResponse(result);

        closeStream();
    }

    private void sendResponse(RequestResult result) {
        try {
            InputStream resultInputStream = IoUtils.createBufferedInputStream(result.getInputStream());

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

    private void closeStream() {
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
