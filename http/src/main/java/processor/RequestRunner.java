package processor;

import java.io.InputStream;
import java.io.OutputStream;

public interface RequestRunner {
    void run(InputStream inputStream, OutputStream outputStream);
}
