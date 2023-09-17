package matcher.converter.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import static com.main.util.IoUtils.createBufferedInputStream;

public class FileConverter implements Converter<File> {
    @Override
    public InputStream convertToInputStream(File file) {
        Objects.requireNonNull(file);
        return doConvertToInputStream(file);
    }

    private InputStream doConvertToInputStream(File fIle) {
        try {
            return createBufferedInputStream(new FileInputStream(fIle));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
