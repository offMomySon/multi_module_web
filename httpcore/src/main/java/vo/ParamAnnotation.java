package vo;

import java.util.Objects;
import java.util.Optional;
import marker.PathVariable;
import marker.RequestParam;

public class ParamAnnotation {
    private final String name;
    private final boolean required;
    private final Optional<String> defaultValue;

    public ParamAnnotation(String name, boolean required, String defaultValue) {
        if (Objects.isNull(name) || name.isEmpty() || name.isBlank()) {
            throw new RuntimeException("invalid name value. value : " + name);
        }

        this.name = name;
        this.required = required;
        this.defaultValue = Optional.ofNullable(defaultValue);
    }

    public static ParamAnnotation from(RequestParam requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new RuntimeException("requestParam is null.");
        }

        String name = requestParam.value();
        boolean required = requestParam.required();
        String defaultValue = requestParam.defaultValue();

        return new ParamAnnotation(name, required, defaultValue);
    }

    public static ParamAnnotation from(PathVariable requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new RuntimeException("requestParam is null.");
        }

        String name = requestParam.value();
        boolean required = requestParam.required();

        return new ParamAnnotation(name, required, null);
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
