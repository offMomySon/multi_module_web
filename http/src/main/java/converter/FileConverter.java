package converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import util.IoUtils;

public class FileConverter implements Converter {
    @Override
    public InputStream convertToInputStream(Object object) throws Exception {
        Objects.requireNonNull(object);
        return convertToInputStream((File) object);
    }

    public InputStream convertToInputStream(File fIle) throws FileNotFoundException {
        return IoUtils.createBufferedInputStream(new FileInputStream(fIle));
    }
}
