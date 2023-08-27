package com.main.task.response;

import com.main.task.annotation.ResponseBody;
import com.main.task.annotation.RestController;
import com.main.util.AnnotationUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContentTypeCreator {
    private final boolean isResponseBodyMethod;
    private final Object methodResult;

    public ContentTypeCreator(boolean isResponseBodyMethod, Object methodResult) {
        Objects.requireNonNull(methodResult);
        this.isResponseBodyMethod = isResponseBodyMethod;
        this.methodResult = methodResult;
    }

    public static ContentTypeCreator from(Method javaMethod, Object methodResult) {
        Objects.requireNonNull(javaMethod);
        Objects.requireNonNull(methodResult);

        Class<?> declaringClass = javaMethod.getDeclaringClass();
        boolean isResponseBodyMethod = AnnotationUtils.exist(javaMethod, ResponseBody.class) ||
            AnnotationUtils.exist(declaringClass, RestController.class);

        return new ContentTypeCreator(isResponseBodyMethod, methodResult);
    }

    public String create() {
        if (isResponseBodyMethod) {
            return "application/json";
        }

        if (methodResult instanceof Path) {
            Path resource = (Path) methodResult;
            FileExtension fileExtension = getFileExtension(resource);
            return fileExtension.getContentType();
        }

        return "text/html";
    }

    private static FileExtension getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        log.info("fileName : {}", fileName);

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new RuntimeException("does not exist file Extension");
        }

        String fileExtension = fileName.substring(dotIndex + 1);
        return FileExtension.find(fileExtension);
    }
}
