package instance;

import com.main.util.AnnotationUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

class AnnotatedObjectRepository2Test {

    @DisplayName("java method 를 가진 object 를 가져옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindByMethodTestSuite")
    void Given_repository_When_findObjectByMethod_Then_foundOptionalObject(Class<?> clazz, Object instance, Method method, boolean result) throws Exception {
        //given
        AnnotatedObjectRepository2 repository = new AnnotatedObjectRepository2(Map.of(clazz, instance));

        //when
        Optional<Object> optionalActual = repository.findObjectByMethod(method);

        //then
        Assertions.assertThat(optionalActual.isPresent()).isEqualTo(result);

        if (optionalActual.isPresent()) {
            Object o = optionalActual.get();

            boolean clazzHasMethod = Arrays.asList(o.getClass().getDeclaredMethods()).contains(method);
            Assertions.assertThat(clazzHasMethod).isTrue();
        }
    }

    @DisplayName("annotated 된 class 를 찾아옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindClassByAnnotatedClassTestSuite")
    void Given_repository_When_findClassByAnnotatedClass_Then_foundOptionalObject(Map<Class<?>, Object> repositoryMap,
                                                                                  Class<?> findAnnotationClazz,
                                                                                  List<Class<?>> result) throws Exception {
        //given
        AnnotatedObjectRepository2 repository = new AnnotatedObjectRepository2(repositoryMap);

        //when
        List<Class<?>> actuals = repository.findClassByAnnotatedClass(findAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
    }

    @DisplayName("annoation class 가 아니면 class 를 찾을 수 없습니다.")
    @Test
    void Given_repository_When_findClassByAnnotatedClass_with_none_Annotation_class_Then_Exception() throws Exception {
        //given
        AnnotatedObjectRepository2 repository = new AnnotatedObjectRepository2(Map.of(TestClass.class, new TestClass(),
                                                                                      AnotherTestClass.class, new AnotherTestClass()));

        //when
        Throwable actual = Assertions.catchThrowable(()-> repository.findClassByAnnotatedClass(TestClass.class));

        //then
        Assertions.assertThat(actual).isNotNull();
    }



    @DisplayName("annotation 이 annotated 된 object 를 찾아옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindAnnotatedObjectByAnnotatedClassTestSuite")
    void Given_repository_When_findAnnotatedObjectByAnnotatedClass_Then_findAnnotatedObject(Map<Class<?>, Object> repositoryMap,
                                                                                            Class<?> findAnnotationClazz,
                                                                                            List<AnnotatedObject> result) throws Exception {
        //given
        AnnotatedObjectRepository2 repository = new AnnotatedObjectRepository2(repositoryMap);

        //when
        List<AnnotatedObject> actuals = repository.findAnnotatedObjectByAnnotatedClass(findAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
    }

    @DisplayName("annoation class 가 아니면 class 를 찾을 수 없습니다.")
    @Test
    void Given_repository_When_findAnnotatedObjectByAnnotatedClass_with_none_Annotation_class_Then_Exception() throws Exception {
        //given
        AnnotatedObjectRepository2 repository = new AnnotatedObjectRepository2(Map.of(TestClass.class, new TestClass(),
                                                                                      AnotherTestClass.class, new AnotherTestClass()));

        //when
        Throwable actual = Assertions.catchThrowable(()-> repository.findAnnotatedObjectByAnnotatedClass(TestClass.class));

        //then
        Assertions.assertThat(actual).isNotNull();
    }
    @DisplayName("class 에 할당할수 있고, annotation 이 annotated 된 object 를 찾아옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindAnnotatedObjectByClassAndAnnotatedClassTestSuite")
    void Given_repository_When_findAnnotatedObjectByClassAndAnnotatedClass_Then_findAnnotatedObject(Map<Class<?>, Object> repositoryMap,
                                                                                                    Class<?> findClazz,
                                                                                                    Class<?> findAnnotationClazz,
                                                                                                    List<AnnotatedObject> result) throws Exception {
        //given
        AnnotatedObjectRepository2 repository = new AnnotatedObjectRepository2(repositoryMap);

        //when
        List<AnnotatedObject> actuals = repository.findAnnotatedObjectByClassAndAnnotatedClass(findClazz, findAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
    }

    @DisplayName("annoation class 가 아니면 class 를 찾을 수 없습니다.")
    @Test
    void Given_repository_When_findAnnotatedObjectByClassAndAnnotatedClass_with_none_Annotation_class_Then_Exception() throws Exception {
        //given
        AnnotatedObjectRepository2 repository = new AnnotatedObjectRepository2(Map.of(TestClass.class, new TestClass(),
                                                                                      AnotherTestClass.class, new AnotherTestClass()));

        //when
        Throwable actual = Assertions.catchThrowable(()-> repository.findAnnotatedObjectByClassAndAnnotatedClass(TestClass.class, TestClass.class));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    private static Stream<Arguments> provideFindByMethodTestSuite() {
        return Stream.of(
            Arguments.of(TestClass.class, new TestClass(), TestClass.getTestMethod(), true),
            Arguments.of(AnotherTestClass.class, new AnotherTestClass(), AnotherTestClass.getTestMethod(), true),
            Arguments.of(TestClass.class, new TestClass(), AnotherTestClass.getTestMethod(), false),
            Arguments.of(AnotherTestClass.class, new AnotherTestClass(), TestClass.getTestMethod(), false)
        );
    }

    private static Stream<Arguments> provideFindClassByAnnotatedClassTestSuite() {
        return Stream.of(
            Arguments.of(
                Map.of(TestClass.class, new TestClass(),
                       AnotherTestClass.class, new AnotherTestClass()),
                TestAnnotation.class,
                List.of(TestClass.class, AnotherTestClass.class)
            ),
            Arguments.of(
                Map.of(TestClass.class, new TestClass(),
                       AnotherTestClass.class, new AnotherTestClass(),
                       NoneAnnotatedClass.class, new NoneAnnotatedClass()),
                TestAnnotation.class,
                List.of(TestClass.class, AnotherTestClass.class)
            ),
            Arguments.of(
                Map.of(AnotherTestClass.class, new AnotherTestClass()),
                TestAnnotation.class,
                List.of(AnotherTestClass.class)
            ),
            Arguments.of(
                Map.of(TestClass.class, new TestClass()),
                TestAnnotation.class,
                List.of(TestClass.class)
            ),
            Arguments.of(
                Map.of(NoneAnnotatedClass.class, new NoneAnnotatedClass()),
                TestAnnotation.class,
                Collections.emptyList()
            )
        );
    }

    private static Stream<Arguments> provideFindAnnotatedObjectByAnnotatedClassTestSuite() {
        TestAnnotation testAnnotation = AnnotationUtils.find(TestClass.class, TestAnnotation.class).orElseThrow();
        TestClass testClassObject = new TestClass();
        AnotherTestClass anotherTestClassObject = new AnotherTestClass();
        NoneAnnotatedClass noneAnnotatedClassObject = new NoneAnnotatedClass();

        AnnotatedObject annotatedTestClassObject = new AnnotatedObject(testAnnotation, testClassObject);
        AnnotatedObject annotatedAnotherTestClassObject = new AnnotatedObject(testAnnotation, anotherTestClassObject);

        return Stream.of(
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject),
                TestAnnotation.class,
                List.of(annotatedTestClassObject,
                        annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestAnnotation.class,
                List.of(annotatedTestClassObject,
                        annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(AnotherTestClass.class, anotherTestClassObject),
                TestAnnotation.class,
                List.of(annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject),
                TestAnnotation.class,
                List.of(annotatedTestClassObject)
            ),
            Arguments.of(
                Map.of(NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestAnnotation.class,
                Collections.emptyList()
            )
        );
    }

    private static Stream<Arguments> provideFindAnnotatedObjectByClassAndAnnotatedClassTestSuite() {
        TestAnnotation testAnnotation = AnnotationUtils.find(TestClass.class, TestAnnotation.class).orElseThrow();

        TestClass testClassObject = new TestClass();
        AnotherTestClass anotherTestClassObject = new AnotherTestClass();
        NoneAnnotatedClass noneAnnotatedClassObject = new NoneAnnotatedClass();

        AnnotatedObject annotatedTestClassObject = new AnnotatedObject(testAnnotation, testClassObject);
        AnnotatedObject annotatedAnotherTestClassObject = new AnnotatedObject(testAnnotation, anotherTestClassObject);

        return Stream.of(
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                Object.class,
                TestAnnotation.class,
                List.of(annotatedTestClassObject,
                        annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestInterface.class,
                TestAnnotation.class,
                List.of(annotatedTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                AnotherTestClass.class,
                TestAnnotation.class,
                List.of(annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestClass.class,
                TestAnnotation.class,
                List.of(annotatedTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                NoneAnnotatedClass.class,
                TestAnnotation.class,
                Collections.emptyList()
            )
        );
    }

    @Retention(RUNTIME)
    @Target(TYPE)
    private @interface TestAnnotation {


    }
    private @interface DoesNotUsedAnnotation {


    }
    private interface TestInterface {


    }
    @TestAnnotation
    public static class TestClass implements TestInterface {

        public void method(int param) {

        }

        public static Method getTestMethod() {
            try {
                return TestClass.class.getMethod("method", int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @TestAnnotation
    public static class AnotherTestClass {

        public void method(int param) {

        }

        public static Method getTestMethod() {
            try {
                return AnotherTestClass.class.getMethod("method", int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static class NoneAnnotatedClass {

        public void method(int param) {

        }

        public static Method getTestMethod() {
            try {
                return AnotherTestClass.class.getMethod("method", int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}