package response.generator;

import dto.ContentType;
import dto.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SingleResponseEncoder extends ResponseEncoder {
    private static final String CONTENT_LENGTH_HEADER = "Content-Length : ";

    public SingleResponseEncoder(Status status, ContentType contentType, InputStream contentInputStream) {
        super(status, contentType, contentInputStream);
    }

    public byte[] encode() {
        byte[] contentBytes = readContent();
        byte[] headerBytes = generateHeader(contentBytes.length);

        byte[] responseBytes = combineEncodeBytes(headerBytes, contentBytes);

        return responseBytes;
    }

    private byte[] generateHeader(int readLength) {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(status.getResponseLine()).append(END_OF_LINE);
        headerBuilder.append(DATE_HEADER).append(new Date()).append(END_OF_LINE);
        headerBuilder.append(contentType.getHeaderContent()).append(END_OF_LINE);
        headerBuilder.append(CONTENT_LENGTH_HEADER).append(readLength).append(END_OF_LINE);
        headerBuilder.append(END_OF_LINE);

        String header = headerBuilder.toString();

        return header.getBytes(UTF_8);
    }

    private byte[] readContent() {
        try {
            return contentInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] combineEncodeBytes(byte[] headerBytes, byte[] contentBytes) {
        byte[] responseBytes = new byte[headerBytes.length + contentBytes.length + 1];

        System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
        System.arraycopy(contentBytes, 0, responseBytes, headerBytes.length, contentBytes.length);

        return responseBytes;
    }
}
