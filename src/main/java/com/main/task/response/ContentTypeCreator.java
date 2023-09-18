package com.main.task.response;

import com.main.task.annotation.ResponseBody;
import com.main.util.AnnotationUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import task.endpoint.EndPointTask;
import task.endpoint.JavaMethodInvokeTask;
import vo.ContentType;
import vo.FileExtension;
import static vo.ContentType.APPLICATION_JSON;
import static vo.ContentType.TEXT_HTML;

@Slf4j
public class ContentTypeCreator {
    private final boolean isRestMethodTask;
    private final Optional<Object> optionalMethodResult;

    public ContentTypeCreator(boolean isRestMethodTask, Optional<Object> optionalMethodResult) {
        Objects.requireNonNull(optionalMethodResult);
        this.isRestMethodTask = isRestMethodTask;
        this.optionalMethodResult = optionalMethodResult;
    }

    public static ContentTypeCreator from(EndPointTask endPointTask, Optional<Object> optionalMethodResult) {
        Objects.requireNonNull(endPointTask);
        Objects.requireNonNull(optionalMethodResult);

        if (optionalMethodResult.isEmpty()) {
            return new ContentTypeCreator(false, optionalMethodResult);
        }

        if (endPointTask instanceof JavaMethodInvokeTask) {
            JavaMethodInvokeTask javaMethodInvokeTask = (JavaMethodInvokeTask) endPointTask;
            Method javaMethod = javaMethodInvokeTask.getJavaMethod();
            boolean isRestMethodTask = AnnotationUtils.exist(javaMethod, ResponseBody.class);
            return new ContentTypeCreator(isRestMethodTask, optionalMethodResult);
        }

        return new ContentTypeCreator(false, optionalMethodResult);
    }

    public Optional<ContentType> create() {
        if (optionalMethodResult.isEmpty()) {
            return Optional.empty();
        }

        if (isRestMethodTask) {
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
