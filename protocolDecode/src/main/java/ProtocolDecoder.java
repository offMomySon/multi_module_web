import java.io.BufferedReader;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import request.ReadRequest;
import request.Request;
import request.UpdateRequest;
import structure.HttpBody;
import structure.HttpHeader;
import structure.Method;
import structure.RequestURI;
import validate.ValidateUtil;
import static io.IoUtils.creatBufferedReader;
import static validate.ValidateUtil.validateNull;

public class ProtocolDecoder {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String EOF_HEADER = "";
    private static final String CONTENT_LENGTH_KEY = "Content-Length";

    public Request decode(InputStream requestInputStream) {
        validateNull(requestInputStream);
        BufferedReader requestBufferedStream = creatBufferedReader(requestInputStream);

        List<String> headers = getHeaderLines(requestBufferedStream);

        String[] requestLineElements = headers.get(0).split(REQUEST_LINE_DELIMITER, 3);

        Method method = Method.find(requestLineElements[0]);
        RequestURI requestURI = RequestURI.from(requestLineElements[1]);
        HttpHeader httpHeader = HttpHeader.from(headers.subList(1, headers.size()));

        if (method == Method.GET) {
            return new ReadRequest(method, requestURI, httpHeader);
        }

        int contentLength = Integer.parseInt(httpHeader.getHeaderValue(CONTENT_LENGTH_KEY).stream().collect(Collectors.toUnmodifiableList()).get(0));
        HttpBody httpBody = HttpBody.from(contentLength, requestInputStream);

        return new UpdateRequest(method, requestURI, httpHeader, httpBody);
    }

    private static List<String> getHeaderLines(BufferedReader requestBufferedReader) {
        List<String> headers = new ArrayList<>();

        String header = EOF_HEADER;
        try {
            while (true) {
                header = requestBufferedReader.readLine();
                if (isEndOfHeader(header)) {
                    return headers;
                }

                headers.add(header);
            }
        } catch (Exception exception) {
            throw new RuntimeException(MessageFormat.format("exeception happen. e : `{}`, e.message : `{}`", exception, exception.getMessage()));
        }
    }

    private static boolean isEndOfHeader(String header) {
        return StringUtils.compare(header, EOF_HEADER) == 0;
    }
}
