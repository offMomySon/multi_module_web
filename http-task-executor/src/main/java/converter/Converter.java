package converter;

import java.io.InputStream;

public interface Converter<T> {
    InputStream convertToInputStream(T object);
}
