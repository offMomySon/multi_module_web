package request;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import util.IoUtils;
import static util.IoUtils.createBufferedOutputStream;

public class ServletResponse implements Closeable {
    private static final String END_OF_LINE = "\r\n";

    private BufferedOutputStream bufferedOutputStream;

    private ServletResponse(BufferedOutputStream bufferedOutputStream) {
        this.bufferedOutputStream = bufferedOutputStream;
    }

    public static ServletResponse from(Socket socket){
        try {
            return new ServletResponse(createBufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendResponse(String message) {
        String httpResponse = new StringBuilder()
            .append(createHeader(message.length()))
            .append(message)
            .append(END_OF_LINE)
            .toString();

        try {
            bufferedOutputStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createHeader(long contentLength) {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("HTTP/1.1 200 OK ").append(END_OF_LINE);
        headerBuilder.append("Content-Length : ").append(contentLength).append(END_OF_LINE);
        headerBuilder.append("Content-Type: ").append("text/html").append(END_OF_LINE);
        headerBuilder.append("Date: ").append(new Date()).append(END_OF_LINE);
        headerBuilder.append("Server: jihun server 1.0 ").append(END_OF_LINE);
        headerBuilder.append(END_OF_LINE);

        return headerBuilder.toString();
    }

    @Override
    public void close() throws IOException {
        bufferedOutputStream.close();
    }
}
