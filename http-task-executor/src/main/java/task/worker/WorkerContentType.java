package task.worker;

import java.text.MessageFormat;
import java.util.Objects;

// 작은 도메인들은 영역이 작기 때문에 정책적인 것들보다, 기능 단위의 객체들을 이용할것이다.
// 어디서 부터 정책적인 영역이 나올 수 있는지?
public enum WorkerContentType {
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

    public static WorkerContentType findByClazz(Class<?> returnClazz) {
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

    public static WorkerContentType findByExtension(String extension) {
        if (Objects.isNull(extension)) {
            throw new RuntimeException("contentType is empty.");
        }

        if (extension.startsWith(".")) {
            extension = extension.substring(extension.indexOf(".") + 1);
        }
        extension = extension.toLowerCase();

        switch (extension) {
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

        throw new RuntimeException(MessageFormat.format("Does not exist match Type. extenstion : `{}`", extension));
    }
}
