package task.endpoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterAndValueMatcherType;

@Slf4j
public class JavaMethodInvokeTask implements EndPointTask {
    private final Object declaringClazzObject;
    private final Method javaMethod;
    private final ParameterAndValueMatcherType[] parameterAndValueMatcherTypes;

    public JavaMethodInvokeTask(Object declaringClazzObject, Method javaMethod, ParameterAndValueMatcherType[] _parameterAndValueMatcherTypes) {
        Objects.requireNonNull(declaringClazzObject);
        Objects.requireNonNull(javaMethod);
        Objects.requireNonNull(_parameterAndValueMatcherTypes);

        Set<Parameter> methodParameters = Arrays.stream(javaMethod.getParameters()).collect(Collectors.toUnmodifiableSet());
        Set<Parameter> otherParameters = Arrays.stream(_parameterAndValueMatcherTypes).map(ParameterAndValueMatcherType::getParameter).collect(Collectors.toUnmodifiableSet());
        boolean doesNotMethodParameters = !methodParameters.containsAll(otherParameters);
        if(doesNotMethodParameters){
            throw new RuntimeException("does not method parameters.");
        }

        this.declaringClazzObject = declaringClazzObject;
        this.javaMethod = javaMethod;
        this.parameterAndValueMatcherTypes = _parameterAndValueMatcherTypes;
    }

    @Override
    public ParameterAndValueMatcherType[] getParameterTypeInfos() {
        return Arrays.copyOf(parameterAndValueMatcherTypes, parameterAndValueMatcherTypes.length);
    }

    @Override
    public Optional<Object> execute(Object[] params) {
        try {
            Object invoke = javaMethod.invoke(declaringClazzObject, params);
            return Optional.ofNullable(invoke);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
