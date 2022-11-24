package response.generator;

import dto.ContentType;
import dto.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ChunkResponseEncoder extends ResponseEncoder {
    private static final String TRANSFER_ENCODING = "Transfer-encoding : Chunked";
    private final byte[] BUFFER = new byte[8192];

    public ChunkResponseEncoder(Status status, ContentType contentType, InputStream contentInputStream) {
        super(status, contentType, contentInputStream);
    }

    @Override
    public byte[] encode() {
        byte[] headerBytes = generateHeader();
        int contentBytes = readContent();
        byte[] chunkInfoBytes = generateChunkInfoBytes(contentBytes);


        byte[] responseBytes = combineEncodeBytes(headerBytes, contentBytes, chunkInfoBytes);

        return responseBytes;
    }

    public boolean isLeftContent() {
        try {
            return contentInputStream.available() != 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] combineEncodeBytes(byte[] headerBytes, int contentBytes, byte[] chunkInfoBytes) {
        byte[] responseBytes = new byte[headerBytes.length + chunkInfoBytes.length + contentBytes + 1];

        System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
        System.arraycopy(chunkInfoBytes, 0, responseBytes, headerBytes.length, chunkInfoBytes.length);
        System.arraycopy(BUFFER, 0, responseBytes, headerBytes.length+ chunkInfoBytes.length, contentBytes);

        return responseBytes;
    }

    private byte[] generateChunkInfoBytes(int contentBytes) {
        StringBuilder chunkSize = new StringBuilder(Integer.toHexString(contentBytes)).append(END_OF_LINE);
        return chunkSize.toString().getBytes(UTF_8);
    }

    private byte[] generateHeader() {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(status.getResponseLine()).append(END_OF_LINE);
        headerBuilder.append(DATE_HEADER).append(new Date()).append(END_OF_LINE);
        headerBuilder.append(contentType.getHeaderContent()).append(END_OF_LINE);
        headerBuilder.append(TRANSFER_ENCODING).append(END_OF_LINE);

        String header = headerBuilder.toString();

        return header.getBytes(UTF_8);
    }

    private int readContent() {
        try {
            return contentInputStream.read(BUFFER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
