package com.main.task.converter.result.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.task.annotation.ResponseBody;
import com.main.task.annotation.RestController;
import com.main.task.converter.result.RestMethodResultConverter;
import com.main.task.converter.result.ResultConverter;
import com.main.task.converter.result.chain.ResultConverterCreatorChain;
import com.main.util.AnnotationUtils;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import task.EndPointTask;
import task.JavaMethodInvokeTask;

@Slf4j
public class RestMethodResultConverterCreator {
    private static final Class<?> REST_CONTROLLER = RestController.class;
    private static final Class<?> RESPONSE_BODY = ResponseBody.class;

    private final ObjectMapper objectMapper;
    private final EndPointTask endPointTask;

    public RestMethodResultConverterCreator(ObjectMapper objectMapper, JavaMethodInvokeTask javaMethodInvokeTask) {
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(javaMethodInvokeTask);
        this.objectMapper = objectMapper;
        this.endPointTask = javaMethodInvokeTask;
    }

    public Optional<ResultConverter> create() {
        boolean doesNotJavaMethodInvokeTask = !(endPointTask instanceof JavaMethodInvokeTask);
        if (doesNotJavaMethodInvokeTask) {
            log.info("Does not javaMethod invoke task. Must be javaMethodInvokeTask");
            return Optional.empty();
        }

        JavaMethodInvokeTask javaMethodInvokeTask = (JavaMethodInvokeTask) endPointTask;
        Method javaMethod = javaMethodInvokeTask.getJavaMethod();
        Class<?> controller = javaMethod.getDeclaringClass();
        boolean doesNotRestMethodTask = !AnnotationUtils.exist(javaMethod, RESPONSE_BODY) && !AnnotationUtils.exist(controller, REST_CONTROLLER);
        if (doesNotRestMethodTask) {
            return Optional.empty();
        }

        RestMethodResultConverter restMethodResultConverter = new RestMethodResultConverter(objectMapper);
        return Optional.of(restMethodResultConverter);
    }
}
