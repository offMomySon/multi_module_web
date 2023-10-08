package task.worker;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;

// 작은 도메인들은 영역이 작기 때문에 정책적인 것들보다, 기능 단위의 객체들을 이용할것이다.
// 어디서 부터 정책적인 영역이 나올 수 있는지?
public enum ContentType {
    EMPTY,
    STRING,
    JSON,
    JPEG,
    GIF,
    PNG,
    HTML,
    TXT,
    CSS,
    JAVASCRIPT,
    CLASS;

    public static ContentType findByClazz(Class<?> returnClazz) {
        if (Objects.isNull(returnClazz)) {
            throw new RuntimeException("returnClazz is empty.");
        }

        if (returnClazz == void.class ||
            returnClazz == Void.class) {
            return EMPTY;
        }

        if (returnClazz == boolean.class ||
            returnClazz == Boolean.class ||
            returnClazz == byte.class ||
            returnClazz == Byte.class ||
            returnClazz == char.class ||
            returnClazz == Character.class ||
            returnClazz == short.class ||
            returnClazz == Short.class ||
            returnClazz == int.class ||
            returnClazz == Integer.class ||
            returnClazz == long.class ||
            returnClazz == Long.class ||
            returnClazz == float.class ||
            returnClazz == Float.class ||
            returnClazz == double.class ||
            returnClazz == Double.class ||
            returnClazz == String.class
        ) {
            return STRING;
        }
        return JSON;
    }

    public static ContentType findByPath(Path resourcePath) {
        if (Objects.isNull(resourcePath)) {
            throw new RuntimeException("Invalid parameter. resourcePath is empty.");
        }

        String fileName = resourcePath.getFileName().toString();
        return findByFileName(fileName);
    }

    public static ContentType findByFileName(String fileName) {
        if (Objects.isNull(fileName) || fileName.isBlank()) {
            throw new RuntimeException("Invalid parameter. fileName is empty.");
        }

        String fileExtension = getFileExtension(fileName);

        switch (fileExtension) {
            case "json":
                return JSON;
            case "jpeg":
            case "jpg":
                return JPEG;
            case "gif":
                return GIF;
            case "png":
                return PNG;
            case "html":
                return HTML;
            case "txt":
                return TXT;
            case "css":
                return CSS;
            case "js":
                return JAVASCRIPT;
            case "class":
                return CLASS;
        }

        throw new RuntimeException(MessageFormat.format("Does not exist match Type. fileName: `{}`, fileExtension: `{}`", fileName, fileExtension));
    }

    private static String getFileExtension(String fileName) {
        int delimiterIndex = fileName.indexOf(".");
        return fileName.substring(delimiterIndex+1);
    }
}
