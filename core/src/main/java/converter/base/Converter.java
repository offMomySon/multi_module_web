package converter.base;

import java.io.InputStream;

public interface Converter {
    InputStream convertToInputStream(Object object);
}
