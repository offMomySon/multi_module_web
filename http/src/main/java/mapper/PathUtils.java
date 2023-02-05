package mapper;

import java.nio.file.Path;
import java.util.Objects;

public class PathUtils {
    public static Class<?> createClass(Path rootPath, Path filePath){
        if(Objects.isNull(filePath) || Objects.isNull( filePath)){
            throw new RuntimeException("path is null.");
        }

        Path jvmPath = rootPath.relativize(filePath);

        return createClassFromJvmPath(jvmPath);
    }

    private static Class<?> createClassFromJvmPath(Path filePath) {
        if(Objects.isNull(filePath)){
            throw new RuntimeException("filePath is null.");
        }

        String fullyQualifiedClassName = filePath.toString()
            .substring(0, filePath.toString().lastIndexOf(".class"))
            .replace("/", ".");

        try {
            return Class.forName(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
