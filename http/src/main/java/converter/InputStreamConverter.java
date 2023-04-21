package converter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import util.IoUtils;

public class InputStreamConverter implements Converter {
    @Override
    public InputStream convertToInputStream(Object object) throws Exception {
        Objects.requireNonNull(object);
        return convertToInputStream((InputStream) object);
    }

    public InputStream convertToInputStream(InputStream inputStream) throws FileNotFoundException {
        return IoUtils.createBufferedInputStream(inputStream);
    }
}
