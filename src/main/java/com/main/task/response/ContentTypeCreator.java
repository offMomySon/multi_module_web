package com.main.task.response;

import com.main.task.annotation.ResponseBody;
import com.main.task.annotation.RestController;
import com.main.util.AnnotationUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import task.JavaMethodInvokeTask;
import task.Task;
import static com.main.task.response.ContentType.APPLICATION_JSON;
import static com.main.task.response.ContentType.TEXT_HTML;

@Slf4j
public class ContentTypeCreator {
    private final boolean isResponseBodyMethod;
    private final Optional<Object> optionalMethodResult;

    public ContentTypeCreator(boolean isResponseBodyMethod, Optional<Object> optionalMethodResult) {
        Objects.requireNonNull(optionalMethodResult);
        this.isResponseBodyMethod = isResponseBodyMethod;
        this.optionalMethodResult = optionalMethodResult;
    }

    public static ContentTypeCreator from(Task task, Optional<Object> optionalMethodResult) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(optionalMethodResult);

        if (optionalMethodResult.isEmpty()) {
            return new ContentTypeCreator(false, optionalMethodResult);
        }

        if (task instanceof JavaMethodInvokeTask) {
            JavaMethodInvokeTask javaMethodInvokeTask = (JavaMethodInvokeTask) task;
            Method javaMethod = javaMethodInvokeTask.getJavaMethod();
            Class<?> declaringClass = javaMethod.getDeclaringClass();
            boolean isResponseBodyMethod = AnnotationUtils.exist(javaMethod, ResponseBody.class) ||
                AnnotationUtils.exist(declaringClass, RestController.class);
            return new ContentTypeCreator(isResponseBodyMethod, optionalMethodResult);
        }

        return new ContentTypeCreator(false, optionalMethodResult);
    }

    public Optional<ContentType> create() {
        if(optionalMethodResult.isEmpty()){
            return Optional.empty();
        }

        if (isResponseBodyMethod) {
            return Optional.of(APPLICATION_JSON);
        }

        Object methodResult = optionalMethodResult.get();
        if (methodResult instanceof Path) {
            Path resource = (Path) methodResult;
            FileExtension fileExtension = extractFileExtension(resource);
            ContentType contentType = fileExtension.getContentType();
            return Optional.of(contentType);
        }

        return Optional.of(TEXT_HTML);
    }

    private static FileExtension extractFileExtension(Path path) {
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
