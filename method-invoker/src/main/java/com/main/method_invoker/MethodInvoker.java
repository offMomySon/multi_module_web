package com.main.method_invoker;

import com.main.util.IoUtils;
import com.main.util.converter.CompositeValueTypeConverter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import static java.util.Objects.isNull;


// 1. request param
// 2. request body
// 3. path variable
// 4. url

// pattern 은 가져온다고 가졍한다.
public class MethodInvoker {
    private static final CompositeValueTypeConverter converter = new CompositeValueTypeConverter();
    // 무엇을 구성해야할까?
    // mehtod 를 실행시키기위한 환경?
    // resource type 각각의 key, value 쌍.
    private final Map<ResourceType, ResourceValues> resourceValuesMap;

    public MethodInvoker(@NonNull Map<ResourceType, ResourceValues> resourceValuesMap) {
        this.resourceValuesMap = Map.copyOf(resourceValuesMap);
    }

    // string - pathvariable, query param
    // inputstream - body
    // 군집을 어떻게?  - http 요청 -> 구체적으로 -> 분류 ->
    // http 설명
    // 1. startline -
    // 2. header
    // 3. body.

    // object, method, param info (resource type, param name, default value).
    public Object invoke(@NonNull Object object, @NonNull Method method, @NonNull ParameterMeta[] parameterMetas) {
        boolean doesNotObjectMethod = Arrays.stream(object.getClass().getDeclaredMethods()).noneMatch(m -> m == method);
        if (doesNotObjectMethod) {
            throw new RuntimeException("Does not method is contained to Object. Must be method is contained to Object.");
        }
        boolean doesNotSameLengthOfParam = method.getParameters().length != parameterMetas.length;
        if (doesNotSameLengthOfParam) {
            throw new RuntimeException("Does not same length of param. Must be same length.");
        }

        Object[] parameterValues = Arrays.stream(parameterMetas)
            .map(pm -> getParameterValue(resourceValuesMap, pm))
            .toArray();
        Object[] values = getValues(method, parameterValues);

        return invoke(object, method, values);
    }

    private static Object invoke(Object object, Method method, Object[] values) {
        try {
            return method.invoke(object, values);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object[] getValues(Method method, Object[] parameterValues) {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            Object parameterValue = parameterValues[i];

            if (parameter.getType() == parameterValue.getClass()
                || parameter.getType().isAssignableFrom(parameterValue.getClass())) {
                values.add(parameterValue);
                continue;
            }

            if (!InputStream.class.isAssignableFrom(parameter.getType())
                && InputStream.class.isAssignableFrom(parameterValue.getClass())) {
                String value = getString((InputStream) parameterValue);
                Object newParameterValue = converter.convertToClazz(value, parameter.getType());
                values.add(newParameterValue);
                continue;
            }

            Object newParameterValue = converter.convertToClazz((String)parameterValue, parameter.getType());
            values.add(newParameterValue);
        }
        return values.toArray();
    }

    private static String getString(InputStream parameterValue) {
        BufferedInputStream bufferedInputStream = IoUtils.createBufferedInputStream(parameterValue);
        try {
            return new String(bufferedInputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getParameterValue(Map<ResourceType, ResourceValues> resourceValuesMap, ParameterMeta parameterMeta) {
        ResourceType resourceType = parameterMeta.getResourceType();
        boolean doesNotContainResourceType = !resourceValuesMap.containsKey(resourceType);
        if (doesNotContainResourceType) {
            return parameterMeta.getDefaultValue();
        }

        ResourceValues resourceValues = resourceValuesMap.get(resourceType);

        String parameterName = parameterMeta.getParameterName();
        Object defaultValue = parameterMeta.getDefaultValue();
        return resourceValues.getOrDefault(parameterName, defaultValue);
    }

    @Getter
    public static class ParameterMeta {
        private final ResourceType resourceType;
        private final String parameterName;
        private final Object defaultValue;

        public ParameterMeta(@NonNull ResourceType resourceType, @NonNull String parameterName, Object defaultValue) {
            if (parameterName.isBlank()) {
                throw new RuntimeException("Does not valid param. Must value not be empty.");
            }

            validateDefaultValue(defaultValue);

            this.resourceType = resourceType;
            this.parameterName = parameterName;
            this.defaultValue = defaultValue;
        }

        private static void validateDefaultValue(Object defaultValue) {
            if (isNull(defaultValue)) {
                return;
            }
            boolean doesNotValidValueType = (defaultValue.getClass() != String.class) && (!InputStream.class.isAssignableFrom(defaultValue.getClass()));
            if (doesNotValidValueType) {
                throw new RuntimeException("Does not valid param. only string or inputStream type possible.");
            }
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class ResourceType {
        private final String value;

        public ResourceType(@NonNull String value) {
            if (value.isBlank()) {
                throw new RuntimeException("Does not valid param. Must not be emtpy value.");
            }
            this.value = value;
        }
    }

    public static class ResourceValues {
        private final Map<String, Object> values;

        public ResourceValues(@NonNull Map<String, Object> values) {
            this.values = values.entrySet().stream()
                .filter(e -> Objects.nonNull(e.getKey()))
                .filter(e -> isValidateValue(e.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (curr, prev) -> curr));
        }

        public Object getOrDefault(@NonNull String key, @NonNull Object defaultValue) {
            if (key.isBlank()) {
                throw new RuntimeException("Does not valid param. Must not be emtpy value.");
            }
            return values.getOrDefault(key, defaultValue);
        }

        public boolean containsKey(@NonNull String key) {
            if (key.isBlank()) {
                throw new RuntimeException("Does not valid param. Must not be emtpy value.");
            }
            return values.containsKey(key);
        }

        private static boolean isValidateValue(Object value) {
            if (isNull(value)) {
                return false;
            }
            return (value.getClass() != String.class) && InputStream.class.isAssignableFrom(value.getClass());
        }
    }
}

















