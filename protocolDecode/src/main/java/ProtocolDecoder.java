import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;
import request.GetRequest;
import request.Request;
import request.PostRequest;
import structure.HttpBody;
import structure.Method;
import structure.RequestURI;
import static io.IoUtils.creatBufferedReader;
import static validate.ValidateUtil.validateNull;

public class ProtocolDecoder {
    private static final String REQUEST_LINE_DELIMITER = " ";

    public Request decode(InputStream requestInputStream) {
        validateNull(requestInputStream);
        BufferedReader requestBufferedStream = creatBufferedReader(requestInputStream);

        String requestLine;
        String[] requestLineElements;
        try {
            requestLine = requestBufferedStream.readLine();
            requestLineElements = requestLine.split(REQUEST_LINE_DELIMITER, 3);
        } catch (IOException e) {
            throw new RuntimeException("fail to read request buffer.");
        }

        Method method = Method.find(requestLineElements[0]);
        RequestURI requestURI = RequestURI.from(requestLineElements[1]);

        return method.createRequest(requestURI, requestInputStream);
    }
}
