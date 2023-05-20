package vo;

import annotation.PathVariable;
import annotation.RequestParam;
import annotation.ValueConstants;
import java.util.Objects;
import java.util.Optional;

public class ParamAnnotationValue {
    private final Class<?> annotationType;
    private final String name;
    private final boolean required;
    private final Optional<String> defaultValue;

    public ParamAnnotationValue(Class<?> annotationType, String name, boolean required, String defaultValue) {
        if (Objects.isNull(name)) {
            throw new RuntimeException("invalid name value. value : " + name);
        }

        defaultValue = Objects.equals(ValueConstants.DEFAULT_NONE, defaultValue) ? null : defaultValue;

        this.annotationType = annotationType;
        this.name = name;
        this.required = required;
        this.defaultValue = Optional.ofNullable(defaultValue);
    }

    public static ParamAnnotationValue from(RequestParam requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new RuntimeException("requestParam is null.");
        }

        String name = requestParam.value();
        boolean required = requestParam.required();
        String defaultValue = requestParam.defaultValue();

        return new ParamAnnotationValue(requestParam.annotationType(), name, required, defaultValue);
    }

    public static ParamAnnotationValue from(PathVariable requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new RuntimeException("requestParam is null.");
        }

        String name = requestParam.value();
        boolean required = requestParam.required();

        return new ParamAnnotationValue(requestParam.annotationType(), name, required, null);
    }

    public Class<?> getAnnotationType() {
        return annotationType;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public Optional<String> getDefaultValue() {
        return defaultValue;
    }
}
