package util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.IoUtils.createBufferedInputStream;

public class ContentFinder {
    public BufferedInputStream createInputStream(Path value) {
        String content = "randomjafklajsdflkjadslfk;jsdal;kfjdsalk;fja;lsdkfjl;ksadjflkasdjfl;ksad";

        ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(content.getBytes(UTF_8));

        return createBufferedInputStream(byteArrayOutputStream);
    }
}
