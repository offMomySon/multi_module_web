package annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// todo
// 적절한 레벨의 추상적인 역할을 설명하는 모듈 이름인가.?
// http - 역할. 외부의 http 응답을 송/수신하는 모듈
// connection - 역할. 요청에 대한 연결을 담당하는 모듈
// context - 역할. 내부 class 의 instance 를 관리하는 모듈.
// web - 역할. 3개의 모듈을 조합하여 web 서비싱하는 모듈.

// http -> connection -> context
// -> web.

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value() default "";

    boolean required() default true;
}
