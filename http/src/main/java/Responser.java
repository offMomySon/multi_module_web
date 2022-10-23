import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import util.IoUtils;
import util.ValidateUtil;
import static util.ValidateUtil.*;

public class Responser {
    private final BufferedOutputStream bufferedOutputStream;

    private Responser(BufferedOutputStream bufferedOutputStream) {
        validateNull(bufferedOutputStream);
        this.bufferedOutputStream = bufferedOutputStream;
    }

    public static Responser create(OutputStream outputStream){
        validateNull(outputStream);
        return new Responser(IoUtils.createBufferedOutputStream(outputStream));
    }

    public

}
