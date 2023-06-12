package converter.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import util.IoUtils;

public class FileConverter implements Converter {
    @Override
    public InputStream convertToInputStream(Object object) {
        Objects.requireNonNull(object);
        return convertToInputStream((File) object);
    }

    public InputStream convertToInputStream(File fIle) {
        try {
            return IoUtils.createBufferedInputStream(new FileInputStream(fIle));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}