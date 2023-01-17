package mapper;

import java.lang.reflect.Method;
import lombok.ToString;

@ToString
public class MethodHandler {
    private final MethodIndicator methodIndicator;
    private final Method method;


    public MethodHandler(MethodIndicator methodIndicator, Method method) {
        this.methodIndicator = methodIndicator;
        this.method = method;
    }

}
