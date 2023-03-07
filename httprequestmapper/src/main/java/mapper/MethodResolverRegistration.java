package mapper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mapper.RequestMappingHttpMethodUrlMethodCreator.HttpMethodUrlMethod;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

@Slf4j
public class MethodResolverRegistration {
//    private final Class<?> classFilerAnnotation;
    public static List<MethodResolver> register2(Class<?> clazz) {
        // 어노테이션 유틸은 클래스 또는 메서드 에 매핑된 어노테이션중, 특정 어노테이션의 존재검사, 찾기 역할을 할 수 있습니다.
        // 가져온 클래스들에서 어노테이션 기반의 필터링을 수행합니다.
        // 대상은 controller, requestMapping 어노테이션 입니다.
        if(AnnotationUtils.doesNotExistAll(clazz, Controller.class, RequestMapping.class)){
            return Collections.emptyList();
        }

        // class 의 methods 를 필터링한다. 필터링 대상읜 requestMapping 어노테이션이다.
        // class, method 를 인자로 받는 ClassAndMethod 를 n 개 생성하여 평탄화 작업을합니다.
        //  n 은 class 갯수 * class 의 method 의 개수 입니다.
        // 평탄화를 하는 이유는 차후에 class 와 method 를 동시에 다루어야하기 때문입니다.
        List<ClassAndMethod> classAndMethods = AnnotationUtils.peekMethods(clazz, RequestMapping.class).stream()
            .map(m -> new ClassAndMethod(clazz, m))
            .collect(Collectors.toUnmodifiableList());

        List<HttpMethodUrlMethod> httpMethodUrlMethods = classAndMethods.stream()
            .flatMap(classAndMethod -> {
                Class<?> _clazz = classAndMethod.getClazz();
                Method method = classAndMethod.getMethod();

                return RequestMappingHttpMethodUrlMethodCreator.create(_clazz, method).stream();
            })
            .collect(Collectors.toUnmodifiableList());

        // httpMethod 가 일치하고, url 의 매칭여부를 체크하는 HttpMethodAndUrlMatcher 를 생성합니다.
        // HttpMethodAndUrlMatcher 가 매칭되면 method 를 출력하는 MethodResolver 를 생성합니다.
        List<MethodResolver> methodResolvers = httpMethodUrlMethods.stream()
            .map(httpMethodAndUrlAndMethod -> {
                HttpMethod httpMethod = httpMethodAndUrlAndMethod.getHttpMethod();
                String url = httpMethodAndUrlAndMethod.getUrl();
                Method method = httpMethodAndUrlAndMethod.getMethod();

                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);

                return new HttpMethodUrlMethodResolver(method, httpMethodUrlMatcher);
            })
            .collect(Collectors.toUnmodifiableList());

        return methodResolvers;
    }


    public static List<MethodResolver> register(List<Class<?>> classes) {
        // 어노테이션 유틸은 클래스 또는 메서드 에 매핑된 어노테이션중, 특정 어노테이션의 존재검사, 찾기 역할을 할 수 있습니다.
        // 가져온 클래스들에서 어노테이션 기반의 필터링을 수행합니다.
        // 대상은 controller, requestMapping 어노테이션 입니다.
        List<Class<?>> controllerClazzs = classes.stream()
            .filter(clazz -> AnnotationUtils.existAll(clazz, Controller.class, RequestMapping.class))
            .collect(Collectors.toUnmodifiableList());

        // class 의 methods 를 필터링한다. 필터링 대상읜 requestMapping 어노테이션이다.
        // class, method 를 인자로 받는 ClassAndMethod 를 n 개 생성하여 평탄화 작업을합니다.
        //  n 은 class 갯수 * class 의 method 의 개수 입니다.
        // 평탄화를 하는 이유는 차후에 class 와 method 를 동시에 다루어야하기 때문입니다.
        List<ClassAndMethod> classAndMethods = controllerClazzs.stream()
            .flatMap(c -> AnnotationUtils.peekMethods(c, RequestMapping.class).stream()
                .map(m -> new ClassAndMethod(c, m)))
            .collect(Collectors.toUnmodifiableList());

        List<HttpMethodUrlMethod> httpMethodUrlMethods = classAndMethods.stream()
            .flatMap(classAndMethod -> {
                Class<?> clazz = classAndMethod.getClazz();
                Method method = classAndMethod.getMethod();

                return RequestMappingHttpMethodUrlMethodCreator.create(clazz, method).stream();
            })
            .collect(Collectors.toUnmodifiableList());

        // httpMethod 가 일치하고, url 의 매칭여부를 체크하는 HttpMethodAndUrlMatcher 를 생성합니다.
        // HttpMethodAndUrlMatcher 가 매칭되면 method 를 출력하는 MethodResolver 를 생성합니다.
        List<MethodResolver> methodResolvers = httpMethodUrlMethods.stream()
            .map(httpMethodAndUrlAndMethod -> {
                HttpMethod httpMethod = httpMethodAndUrlAndMethod.getHttpMethod();
                String url = httpMethodAndUrlAndMethod.getUrl();
                Method method = httpMethodAndUrlAndMethod.getMethod();

                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);

                return new HttpMethodUrlMethodResolver(method, httpMethodUrlMatcher);
            })
            .collect(Collectors.toUnmodifiableList());

        return methodResolvers;
    }

    @Getter
    private static class ClassAndMethod {
        private final Class<?> clazz;
        private final Method method;

        public ClassAndMethod(Class<?> clazz, Method method) {
            this.clazz = clazz;
            this.method = method;
        }
    }
}
