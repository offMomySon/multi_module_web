package converter;

import java.io.InputStream;

public interface Converter {
    InputStream convertToInputStream(Object object) throws Exception;
}
