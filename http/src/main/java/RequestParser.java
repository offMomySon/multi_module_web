import config.IpAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import request.Uri;
import util.ValidateUtil;
import static util.IoUtils.creatBufferedReader;

public class RequestParser {
    private final Uri uri;
    private final IpAddress remoteAddress;

    private RequestParser(Uri uri, IpAddress remoteAddress) {
        ValidateUtil.validateNull(uri);
        ValidateUtil.validateNull(remoteAddress);

        this.uri = uri;
        this.remoteAddress = remoteAddress;
    }

    public static RequestParser parse(InputStream inputStream, InetSocketAddress socketAddress ) throws IOException {
        BufferedReader bufferedInputStream = creatBufferedReader(inputStream);
        String requestLine = bufferedInputStream.readLine();
        String[] splitRequestLine = requestLine.split(" ");

        Uri uri = Uri.from(splitRequestLine[1]);
        IpAddress remoteAddress = IpAddress.from(socketAddress);

        return new RequestParser(uri, remoteAddress);
    }

    public Uri getUri() {
        return uri;
    }

    public IpAddress getRemoteAddress() {
        return remoteAddress;
    }
}
