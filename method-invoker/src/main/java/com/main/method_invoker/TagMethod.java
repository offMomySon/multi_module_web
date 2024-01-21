package com.main.method_invoker;

import com.main.method_invoker.ParameterValueRepository.ParameterValue;
import com.main.util.converter.CompositeValueTypeConverter;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;
import lombok.NonNull;
import static com.main.method_invoker.ParameterValueRepository.ParameterName;
import static java.util.Objects.isNull;

// parameter 에서 필요한 데이터를 추출한다.
public class TagMethod {
    private final String[] customNames;
    private final Object object;
    private final Method method;

    public TagMethod(@NonNull Object object, @NonNull Method method, @NonNull String[] customNames) {
        boolean doesNotDeclaredMethod = Arrays.stream(object.getClass().getDeclaredMethods()).noneMatch(dm -> dm.equals(method));
        if (doesNotDeclaredMethod) {
            throw new RuntimeException("Invalid parameter. Must be declared method.");
        }
        if (customNames.length != method.getParameters().length) {
            throw new RuntimeException("Invalid parameter. Must be same length.");
        }
        this.object = object;
        this.method = method;
        this.customNames = Arrays.copyOf(customNames, customNames.length);
    }

    public ParameterValue[] extractParameterValues(@NonNull ParameterValueRepository parameterValueRepository) {
        Parameter[] parameters = method.getParameters();
        ParameterValue[] values = new ParameterValue[parameters.length];
        for (int index = 0; index < parameters.length; index++) {
            ParameterName parameterName = new ParameterName(customNames[index], parameters[index].getName());
            Optional<ParameterValue> value = parameterValueRepository.findValueByParameterName(parameterName);
            values[index] = value.orElse(null);
        }
        return values;
    }

    public MethodDataInjector createInjector(){
        return new MethodDataInjector(this.object, this.method);
    }

    // method 를 실행시킨다.
    public static class MethodInvoker {
        private final Object object;
        private final Method method;
        private final Object[] values;

        public MethodInvoker(@NonNull Object object, @NonNull Method method, @NonNull Object[] values) {
            boolean doesNotDeclaredMethod = Arrays.stream(object.getClass().getDeclaredMethods()).noneMatch(dm -> dm.equals(method));
            if (doesNotDeclaredMethod) {
                throw new RuntimeException("Invalid parameter. Must be declared method.");
            }
            this.object = object;
            this.method = method;
            this.values = Arrays.copyOf(values, values.length);
        }

        public Object invoke() {
            try {
                return method.invoke(object, values);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Method 에 필요한 데이터를 주입하는 역할.
    public static class MethodDataInjector {
        private final Object object;
        private final Method method;

        public MethodDataInjector(@NonNull Object object, @NonNull Method method) {
            this.object = object;
            this.method = method;
        }

        public MethodInvoker inject(@NonNull ParameterValue[] values) {
            if (method.getParameters().length != values.length) {
                throw new RuntimeException("Invalid parameter. Must be values and parameter length is same.");
            }

            Object[] convertedValues = new Object[values.length];

            Class<?>[] parameterTypes = Arrays.stream(method.getParameters()).map(Parameter::getType).toArray(Class<?>[]::new);
            for (int index = 0; index < parameterTypes.length; index++) {
                ParameterValue value = values[index];
                Class<?> parameterType = parameterTypes[index];
                convertedValues[index] = convertValueType(value, parameterType);
            }

            return new MethodInvoker(object, method, convertedValues);
        }

        private static Object convertValueType(ParameterValue parameterValue, Class<?> targetType) {
            if (isNull(parameterValue)) {
                return null;
            }

            Class<?> valueType = parameterValue.getType();

            if (targetType == valueType || targetType.isAssignableFrom(valueType)) {
                return parameterValue.getValue();
            }

            if (valueType == String.class) {
                String value = (String) parameterValue.getValue();
                return CompositeValueTypeConverter.convertToClazz(value, targetType);
            }

            if(InputStream.class.isAssignableFrom(valueType)){
                InputStream value = (InputStream) parameterValue.getValue();
                return CompositeValueTypeConverter.convertToClazz(value, targetType);
            }

            throw new RuntimeException("dose not possible convert type.");
        }
    }

}
























