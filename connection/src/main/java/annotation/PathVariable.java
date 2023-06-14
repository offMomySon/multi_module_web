package annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// todo
//  적절한 레벨의 추상적인 역할을 설명하는 모듈 이름인가.?
//  내가 느끼기에는 http, context 는 기능적인 관점의 네이밍, 역할은 아니고 적절한 레벨의 추상적인 역할을 설명하고 있다.
//  -> 왜 기능적인 관점이 아닐까?
//     * 역할에 집중된 단어가 아니라, 하나의 개념을 내포한 단어 이다.
//     * 개념을 내포한 단어가, 개념을 바탕으로 모듈의 맥락을 표현하며 맥락에서 어떤 역할을 수행하는지 자연스럽게 설명해준다.
//  [http] 외부의 http 응답을 송/수신 하며 관련된 처리를 관장하는 모듈
//  [context] 시스템에 필요한 요소들의 생성,관리하는 모듈.
//  .
//  하지만, [connection] 은 약간 애매하다.
//  * 개념도 애매하고
//  * 개념을 바탕으로 모듈의 맥락을 표현하지 못한다.
//  * 맥락을 설명하지 못하기 때문에 어떤 역할을 수행하는지 설명하지 못한다.
//  -> 하지만 어떤 역할을 가지는것은 인지가 된다. [ 역할. - method 매칭, 값 반환 처리를 관장하는 역할. ]
//  -> 맥락을 정의하고 싶으나 적절한것이 떠오르지 않는다.
//  -> 맥락을 가질만한 모듈이 아닌가?
//
//  .
//  [Hierachy]
//  http, connection, context
//  web.

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value() default "";

    boolean required() default true;
}
