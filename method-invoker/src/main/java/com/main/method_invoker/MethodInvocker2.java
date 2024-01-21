package com.main.method_invoker;

import com.main.method_invoker.TagMethod.MethodDataInjector;
import lombok.NonNull;
import static com.main.method_invoker.ParameterValueRepository.*;

public class MethodInvocker2 {
    private final TagMethod tagMethod;

    public MethodInvocker2(TagMethod tagMethod) {
        this.tagMethod = tagMethod;
    }

    // [parameter value 로 method 를 실행한다.]
    // 1. parameter value 들은 모두 가져왔다.
    // 2. 타입은 inputstream, string 이다.
    // 3. method 의 parameter type 은 int, long , inputstream, body 등 다양하다.
    // 4. string, inputstream -> int, long, string, inputstream 으로 변환해야한다.
    // 6. 변환된 param value 로 method 를 invoke 한다.


    // 요청 parameter 의 형을 method 에 어떻게 주입시킬것이냐?
    // 유저의 요청에 2가지 타입(stirng, inputstream) 이 존재할것이라 가졍하고, 이 타입을 method parameter 타입에 맞게 변환 해야한다.

    // 1. 메서드 파라미터의 타입, 사용자 파라미터의 타입 비교
    // 2. 메서드 파라미터 타입에 맞게 사용자 파라미터를 변환.
    public Object invoke(@NonNull ParameterValueRepository repository) {
        MethodDataInjector injector = tagMethod.createInjector();
        ParameterValue[] parameterValues = tagMethod.extractParameterValues(repository);

        TagMethod.MethodInvoker inject = injector.inject(parameterValues);

        return inject.invoke();
    }
}
