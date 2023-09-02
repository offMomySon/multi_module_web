package com.main.task.response;

import com.main.task.annotation.ResponseBody;
import com.main.task.annotation.RestController;
import com.main.util.AnnotationUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import task.HttpTask;
import task.JavaMethodTask;

@Slf4j
public class ContentTypeCreator {
    private final boolean isResponseBodyMethod;
    private final Object methodResult;

    public ContentTypeCreator(boolean isResponseBodyMethod, Object methodResult) {
        Objects.requireNonNull(methodResult);
        this.isResponseBodyMethod = isResponseBodyMethod;
        this.methodResult = methodResult;
    }

    public static ContentTypeCreator from(HttpTask httpTask, Object methodResult) {
        Objects.requireNonNull(httpTask);
        Objects.requireNonNull(methodResult);

        if (httpTask instanceof JavaMethodTask) {
            JavaMethodTask javaMethodTask = (JavaMethodTask) httpTask;
            Method javaMethod = javaMethodTask.getJavaMethod();
            Class<?> declaringClass = javaMethod.getDeclaringClass();
            boolean isResponseBodyMethod = AnnotationUtils.exist(javaMethod, ResponseBody.class) ||
                AnnotationUtils.exist(declaringClass, RestController.class);
            return new ContentTypeCreator(isResponseBodyMethod, methodResult);
        }

        return new ContentTypeCreator(false, methodResult);
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
