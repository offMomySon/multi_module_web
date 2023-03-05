package mapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

@Slf4j
public class MethodResolverCreator {
    public MethodResolver create(List<Class<?>> classes) {
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
                .map(m -> new ClassAndMethod(c, m)) )
            .collect(Collectors.toUnmodifiableList());

        // class, method 에서 특정 어노테이션을 가져옵니다. 해당 어노테이션은 requestMapping 어노테이션입니다.
        // 가져오는 이유는 메소드를 지정하는데 필요한 url 과 httpmethod 의 정보를 얻기 위해서 입니다.
        List<RequestMappingMethod> requestMappingMethods = classAndMethods.stream()
            .map(classAndMethod -> {
                RequestMapping classRequestMapping = AnnotationUtils.find(classAndMethod.getClazz(), RequestMapping.class).orElseThrow();
                RequestMapping methodRequestMapping = AnnotationUtils.find(classAndMethod.getMethod(), RequestMapping.class).orElseThrow();
                Method method = classAndMethod.getMethod();

                return new RequestMappingMethod(classRequestMapping, methodRequestMapping, method);
            })
            .collect(Collectors.toUnmodifiableList());

        // httpMethod, url, method 를 인자로 받는 HttpMethodAndUrlAndMethod 을 n 개 생성하여 평탄화 작업을 수행합니다.
        // n 은 httpMethod, classUrl, methodUrl 의 카타시안 곱입니다.
        List<HttpMethodAndUrlAndMethod> httpMethodAndUrlAndMethods = requestMappingMethods.stream()
            .flatMap(requestMappingMethod -> {
                List<HttpMethod> httpMethods = Arrays.stream(requestMappingMethod.getMethodRequestMapping().method()).collect(Collectors.toUnmodifiableList());
                List<String> classUrls = Arrays.stream(requestMappingMethod.getClassRequestMapping().value()).collect(Collectors.toUnmodifiableList());
                List<String> methodUrls = Arrays.stream(requestMappingMethod.getClassRequestMapping().value()).collect(Collectors.toUnmodifiableList());
                Method method = requestMappingMethod.getMethod();

                return classUrls.stream()
                    .flatMap(classUrl -> methodUrls.stream()
                        .map(methodUrl -> classUrl + methodUrl))
                    .flatMap(url -> httpMethods.stream()
                        .map(httpMethod -> new HttpMethodAndUrlAndMethod(httpMethod, url, method)));
            })
            .collect(Collectors.toUnmodifiableList());

        // httpMethod 가 일치하고, url 의 매칭여부를 체크하는 HttpMethodAndUrlMatcher 를 생성합니다.
        // HttpMethodAndUrlMatcher 가 매칭되면 method 를 출력하는 MethodResolver 를 생성합니다.
        List<MethodResolver> methodResolvers = httpMethodAndUrlAndMethods.stream()
            .map(httpMethodAndUrlAndMethod -> {
                HttpMethod httpMethod = httpMethodAndUrlAndMethod.getHttpMethod();
                String url = httpMethodAndUrlAndMethod.getUrl();
                Method method = httpMethodAndUrlAndMethod.getMethod();

                HttpMethodUrlMatcher httpMethodUrlMatcher = new HttpMethodUrlMatcher(httpMethod, url);

                return new HttpMethodUrlMethodResolver(method, httpMethodUrlMatcher);
            })
            .collect(Collectors.toUnmodifiableList());

        return new CompositedMethodResolver(methodResolvers);
    }

    private static Stream<Method> filterMethodStream(Class<?> c, Class<?> annotationClazz) {
        return Arrays.stream(c.getMethods())
            .filter(m -> AnnotationUtils.exist(m, annotationClazz));
    }

    @Getter
    private static class RequestMappingMethod {
        private final RequestMapping classRequestMapping;
        private final RequestMapping methodRequestMapping;
        private final Method method;

        public RequestMappingMethod(RequestMapping classRequestMapping, RequestMapping methodRequestMapping, Method method) {
            this.classRequestMapping = classRequestMapping;
            this.methodRequestMapping = methodRequestMapping;
            this.method = method;
        }
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

    @Getter
    private static class HttpMethodAndUrlAndMethod {
        private final HttpMethod httpMethod;
        private final String url;
        private final Method method;

        public HttpMethodAndUrlAndMethod(HttpMethod httpMethod, String url, Method method) {
            this.httpMethod = httpMethod;
            this.url = url;
            this.method = method;
        }
    }
}
