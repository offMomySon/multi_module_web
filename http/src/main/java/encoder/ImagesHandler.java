package encoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;

public class ImagesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange arg0) throws IOException {
        File file=new File("/root/images/test.gif");
        BufferedImage bufferedImage= ImageIO.read(file);

        WritableRaster writableRaster=bufferedImage.getRaster();
        DataBufferByte data=(DataBufferByte) writableRaster.getDataBuffer();

        arg0.sendResponseHeaders(200, data.getData().length);
        OutputStream outputStream=arg0.getResponseBody();
        Files.copy(file.toPath(), outputStream);
        outputStream.write(data.getData());
        outputStream.close();
    }
}