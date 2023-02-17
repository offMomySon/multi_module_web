package mapper;

import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;

/**
 * DTO
 */
@Getter
public class FilteredClassMethods {
    private final Class<?> clazz;
    private final List<Method> methods;

    public FilteredClassMethods(Class<?> clazz, List<Method> methods) {
        this.clazz = clazz;
        this.methods = methods;
    }


}
