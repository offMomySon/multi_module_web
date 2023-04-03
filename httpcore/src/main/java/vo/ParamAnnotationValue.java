package vo;

import java.util.Objects;
import java.util.Optional;
import marker.PathVariable;
import marker.RequestParam;
import marker.ValueConstants;

public class ParamAnnotationValue {
    private final String name;
    private final boolean required;
    private final Optional<String> defaultValue;

    public ParamAnnotationValue(String name, boolean required, String defaultValue) {
        if (Objects.isNull(name)) {
            throw new RuntimeException("invalid name value. value : " + name);
        }

        defaultValue = Objects.equals(ValueConstants.DEFAULT_NONE, defaultValue) ? null : defaultValue;

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

        return new ParamAnnotationValue(name, required, defaultValue);
    }

    public static ParamAnnotationValue from(PathVariable requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new RuntimeException("requestParam is null.");
        }

        String name = requestParam.value();
        boolean required = requestParam.required();

        return new ParamAnnotationValue(name, required, null);
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