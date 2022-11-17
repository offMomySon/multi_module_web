package structure;

import java.text.MessageFormat;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import request.Request;

public enum Method {
    POST {
        @Override
        public Request createRequest() {
            return null;
        }
    }, GET{
        @Override
        public Request createRequest() {
            return null;
        }
    },
    DELETE{
        @Override
        public Request createRequest() {
            return null;
        }
    },
    PUT {
        @Override
        public Request createRequest() {
            return null;
        }
    };

    public static Method find(String name) {
        return Arrays.stream(values())
            .filter(value -> StringUtils.equalsIgnoreCase(value.name(), name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Not exist method. name = `{}`", name)));
    }

    public abstract Request createRequest();
}
