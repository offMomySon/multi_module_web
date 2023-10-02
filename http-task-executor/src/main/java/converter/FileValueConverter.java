package converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import static com.main.util.IoUtils.createBufferedInputStream;

public class FileValueConverter implements ValueConverter {
    @Override
    public InputStream convertToInputStream(Object file) {
        Objects.requireNonNull(file);
        return doConvertToInputStream((File) file);
    }

    @Override
    public Object convertToClazz(String value) {
        Objects.requireNonNull(value);
        return new File(value);
    }

    private InputStream doConvertToInputStream(File fIle) {
        try {
            return createBufferedInputStream(new FileInputStream(fIle));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
