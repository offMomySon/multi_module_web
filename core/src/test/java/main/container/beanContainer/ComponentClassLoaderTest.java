package main.container.beanContainer;

import container.ComponentClassLoader;
import container.Container;
import container.annotation.Controller;
import container.annotation.Repository;
import container.annotation.Service;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

class ComponentClassLoaderTest {
    @DisplayName("@Component 어노테이션이 붙은 class 를 load 합니다.")
    @Test
    void test0() throws Exception {
        //given
        Set<Class<?>> expect = Set.of(Controller1.class,
                Service1.class,
                Service2.class,
                Repository1.class,
                Repository2.class);

        ComponentClassLoader componentClassLoader = new ComponentClassLoader(Controller1.class);

        //when
        Set<Class<?>> actual = componentClassLoader.load(Container.empty()).keySet();

        //then
        Assertions.assertThat(actual).containsAll(expect);
    }

    @DisplayName("순환참조 class 가 존재하면, exception 이 발생합니다.")
    @ParameterizedTest
    @MethodSource("provideCircularRefClasses")
    void test1(Class<?> clazz) throws Exception {
        //given
        ComponentClassLoader componentClassLoader = new ComponentClassLoader(clazz);

        //when
        Throwable actual = Assertions.catchThrowable(() -> componentClassLoader.load(Container.empty()));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("class 에 component class 가 존재하지 않으면 exception 이 발생합니다.")
    @Test
    void test3() throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new ComponentClassLoader(DoesNotHasComponentAnnotationClass.class));

        //then
        Assertions.assertThat(actual).isNotNull();

    }

    public static Stream<Arguments> provideCircularRefClasses() {
        return Stream.of(
                Arguments.of(SelfRefController.class),
                Arguments.of(CircularRefController.class)
        );
    }

    public static class DoesNotHasComponentAnnotationClass {

    }

    @Controller
    public static class SelfRefController {
        private final SelfRefController selfRefController;

        public SelfRefController(SelfRefController selfRefController) {
            this.selfRefController = selfRefController;
        }
    }

    @Controller
    public static class CircularRefController {
        private final CircularRefDomain circuralRefDomain;

        public CircularRefController(CircularRefDomain circuralRefDomain) {
            this.circuralRefDomain = circuralRefDomain;
        }
    }

    @Service
    private static class CircularRefDomain {
        private final CircuralRefService circuralRefService;

        private CircularRefDomain(CircuralRefService circuralRefService) {
            this.circuralRefService = circuralRefService;
        }
    }

    @Service
    private static class CircuralRefService {
        private final CircuralRefRepository circuralRefRepository;

        private CircuralRefService(CircuralRefRepository circuralRefRepository) {
            this.circuralRefRepository = circuralRefRepository;
        }
    }

    @Repository
    private static class CircuralRefRepository {
        private final CircularRefController circularRefController;

        private CircuralRefRepository(CircularRefController circularRefController) {
            this.circularRefController = circularRefController;
        }
    }

    @Controller
    public static class Controller1 {
        private final Service1 service1;
        private final Service2 service2;

        public Controller1(Service1 service1, Service2 service2) {
            this.service1 = service1;
            this.service2 = service2;
        }
    }

    @Service
    public static class Service1 {
        private final Repository1 repository1;
        private final Repository2 repository2;

        public Service1(Repository1 repository1, Repository2 repository2) {
            this.repository1 = repository1;
            this.repository2 = repository2;
        }
    }

    @Service
    public static class Service2 {
        private final Repository1 repository1;
        private final Repository2 repository2;

        public Service2(Repository1 repository1, Repository2 repository2) {
            this.repository1 = repository1;
            this.repository2 = repository2;
        }
    }

    @Repository
    public static class Repository1 {
    }

    @Repository
    public static class Repository2 {
    }
}