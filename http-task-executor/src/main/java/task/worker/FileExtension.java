package task.worker;

import java.awt.datatransfer.MimeTypeParseException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public enum FileExtension {
    JPG,
    GIF,
    TXT,
    JSON;

    public static FileExtension find(String fileExtension) {
        Objects.requireNonNull(fileExtension);

        String newFileExtension = delimitDot(fileExtension);

        return Arrays.stream(FileExtension.values())
            .filter(value -> value.name().equalsIgnoreCase(newFileExtension))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(MessageFormat.format("Does not exist match fileExtension. Find fileExtension : `{}`", fileExtension)));
    }

    private static String delimitDot(String fileExtension) {
        if (fileExtension.startsWith(".")) {
            fileExtension = fileExtension.substring(fileExtension.indexOf("."));
        }
        return fileExtension;
    }
}
