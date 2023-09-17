package matcher.converter.base;

import java.io.InputStream;
import java.util.Objects;
import static com.main.util.IoUtils.createBufferedInputStream;

public class InputStreamConverter implements Converter {
    @Override
    public InputStream convertToInputStream(Object object) {
        Objects.requireNonNull(object);
        return convertToInputStream((InputStream) object);
    }

    public InputStream convertToInputStream(InputStream inputStream) {
        return createBufferedInputStream(inputStream);
    }
}
