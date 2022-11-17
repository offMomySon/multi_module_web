package structure;

import io.IoUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import request.DeleteRequest;
import request.GetRequest;
import request.PostRequest;
import request.PutRequest;
import request.Request;
import static validate.ValidateUtil.validateNull;

public enum Method {
    POST {
        @Override
        public Request createRequest(RequestURI requestUri, InputStream inputStream) {
            validateNull(requestUri);
            validateNull(inputStream);

            BufferedReader bufferedReader = IoUtils.creatBufferedReader(inputStream);

            List<String> headerLines = Method.generateHeaderLines(bufferedReader);

            HttpHeader httpHeader = HttpHeader.from(headerLines);
            HttpBody httpBody = HttpBody.from(httpHeader.getContentLength(), inputStream);

            return new PostRequest(requestUri, httpHeader, httpBody);
        }
    },
    GET {
        @Override
        public Request createRequest(RequestURI requestUri, InputStream inputStream) {
            validateNull(requestUri);
            validateNull(inputStream);

            BufferedReader bufferedReader = IoUtils.creatBufferedReader(inputStream);

            List<String> headerLines = Method.generateHeaderLines(bufferedReader);

            HttpHeader httpHeader = HttpHeader.from(headerLines);

            return new GetRequest(requestUri, httpHeader);
        }
    },
    DELETE {
        @Override
        public Request createRequest(RequestURI requestUri, InputStream inputStream) {
            validateNull(requestUri);
            validateNull(inputStream);

            BufferedReader bufferedReader = IoUtils.creatBufferedReader(inputStream);

            List<String> headerLines = Method.generateHeaderLines(bufferedReader);

            HttpHeader httpHeader = HttpHeader.from(headerLines);
            HttpBody httpBody = HttpBody.from(httpHeader.getContentLength(), inputStream);

            return new DeleteRequest(requestUri, httpHeader, httpBody);
        }
    },
    PUT {
        @Override
        public Request createRequest(RequestURI requestUri, InputStream inputStream) {
            validateNull(requestUri);
            validateNull(inputStream);

            BufferedReader bufferedReader = IoUtils.creatBufferedReader(inputStream);

            List<String> headerLines = Method.generateHeaderLines(bufferedReader);

            HttpHeader httpHeader = HttpHeader.from(headerLines);
            HttpBody httpBody = HttpBody.from(httpHeader.getContentLength(), inputStream);

            return new PutRequest(requestUri, httpHeader, httpBody);
        }
    };

    private static final String EOF_HEADER = "";

    public static Method find(String name) {
        return Arrays.stream(values())
            .filter(value -> StringUtils.equalsIgnoreCase(value.name(), name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Not exist method. name = `{}`", name)));
    }

    private static List<String> generateHeaderLines(BufferedReader requestBufferedReader) {
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

    public abstract Request createRequest(RequestURI requestUri, InputStream inputStream);
}
