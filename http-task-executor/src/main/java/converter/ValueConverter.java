package converter;

import java.io.InputStream;

public interface ValueConverter {
    InputStream convertToInputStream(Object object);

    Object convertToClazz(String value);
}
