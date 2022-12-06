package response;

import dto.ResponseStatus;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static io.IoUtils.createBufferedInputStream;
import static io.IoUtils.createBufferedOutputStream;
import static io.IoUtils.createBufferedWriter;
import static validate.ValidateUtil.validate;
import static validate.ValidateUtil.validateNull;

public class HttpResponser {
    private static final String END_OF_LINE = "\r\n";
    private static final String KEY_VALUE_DELIMITER = ":";
    private static final String VALUE_DELIMITER = ";";

    private final byte[] BUFFER = new byte[8192];
    private final BufferedWriter bufferedWriter;
    private final BufferedOutputStream bufferedOutputStream;

    private ResponseStatus responseStatus;
    private Map<String, Set<String>> headers;
    private BufferedInputStream bodyInputStream;

    public HttpResponser(OutputStream outputStream) {
        validateNull(outputStream);

        this.bufferedWriter = createBufferedWriter(outputStream);
        this.bufferedOutputStream = createBufferedOutputStream(outputStream);
    }

    public void send() {
        try {
            if (Objects.nonNull(responseStatus)) {
                bufferedWriter.write(responseStatus.getStatusLine());
            }

            if (Objects.nonNull(headers)){
                String header = generateHeader(headers);
                bufferedWriter.write(header);
            }

            bufferedWriter.write(END_OF_LINE);

            if (Objects.nonNull(bodyInputStream)) {
                while (bodyInputStream.available() != 0) {
                    int read = bodyInputStream.read(BUFFER);
                    bufferedOutputStream.write(BUFFER, 0, read);
                }
            }

            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateHeader(Map<String, Set<String>> headers) {
        StringBuilder headerBuilder = new StringBuilder();

        for (String key : headers.keySet()) {
            headerBuilder.append(key).append(KEY_VALUE_DELIMITER);

            Iterator<String> iterator = headers.get(key).iterator();
            while (iterator.hasNext()) {
                headerBuilder.append(iterator.next());

                if (iterator.hasNext()) {
                    headerBuilder.append(VALUE_DELIMITER);
                }
            }
            headerBuilder.append(END_OF_LINE);
        }

        return headerBuilder.toString();
    }

    public HttpResponser responseStatus(ResponseStatus responseStatus) {
        validateNull(responseStatus);
        this.responseStatus = responseStatus;
        return this;
    }

    public HttpResponser header(Map<String, Set<String>> headers) {
        validateNull(headers);
        this.headers = headers;
        return this;
    }

    public HttpResponser body(InputStream bodyInputStream) {
        validateNull(bodyInputStream);
        this.bodyInputStream = createBufferedInputStream(bodyInputStream);
        return this;
    }
}
