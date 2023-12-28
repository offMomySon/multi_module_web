package instance;

import com.main.util.AnnotationUtils;
import instance.AnnotatedObjectRepository.AnnotatedMethod;
import instance.AnnotatedObjectRepository.AnnotatedObjectMethod;
import instance.AnnotatedObjectRepository.AnnotatedObject;
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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

class AnnotatedObjectRepositoryTest {

    @DisplayName("java method 를 가진 object 를 가져옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindByMethodTestSuite")
    void Given_repository_When_findObjectByMethod_Then_foundOptionalObject(Class<?> clazz, Object instance, Method method, boolean result) throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(Map.of(clazz, instance));

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
    void Given_repository_When_findClassByAnnotationClass_Then_foundOptionalObject(Map<Class<?>, Object> repositoryMap,
                                                                                   Class<?> findAnnotationClazz,
                                                                                   List<Class<?>> result) throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(repositoryMap);

        //when
        List<Class<?>> actuals = repository.findClassByClassAnnotatedClass(findAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
    }

    @DisplayName("annoation class 가 아니면 class 를 찾을 수 없습니다.")
    @Test
    void Given_repository_When_findClassByAnnotationClass_with_none_Annotation_class_Then_Exception() throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(Map.of(TestClass.class, new TestClass(),
                                                                                    AnotherTestClass.class, new AnotherTestClass()));

        //when
        Throwable actual = Assertions.catchThrowable(() -> repository.findClassByClassAnnotatedClass(TestClass.class));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("annotation 이 annotated 된 object 를 찾아옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindAnnotatedObjectByAnnotatedClassTestSuite")
    void Given_repository_When_findAnnotatedObjectByAnnotationClass_Then_findAnnotatedObject(Map<Class<?>, Object> repositoryMap,
                                                                                             Class<?> findAnnotationClazz,
                                                                                             List<AnnotatedObject> result) throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(repositoryMap);

        //when
        List<AnnotatedObject> actuals = repository.findAnnotatedObjectByClassAnnotatedClazz(findAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
    }

    @DisplayName("annoation class 가 아니면 class 를 찾을 수 없습니다.")
    @Test
    void Given_repository_When_findAnnotatedObjectByAnnotationClass_with_none_Annotation_class_Then_Exception() throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(Map.of(TestClass.class, new TestClass(),
                                                                                    AnotherTestClass.class, new AnotherTestClass()));

        //when
        Throwable actual = Assertions.catchThrowable(() -> repository.findAnnotatedObjectByClassAnnotatedClazz(TestClass.class));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("class 에 할당할수 있고, annotation 이 annotated 된 object 를 찾아옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindAnnotatedObjectByClassAndAnnotatedClassTestSuite")
    void Given_repository_When_findAnnotatedObjectByClassAndAnnotationClass_Then_findAnnotatedObject(Map<Class<?>, Object> repositoryMap,
                                                                                                     Class<?> findClazz,
                                                                                                     Class<?> findAnnotationClazz,
                                                                                                     List<AnnotatedObject> result) throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(repositoryMap);

        //when
        List<AnnotatedObject> actuals = repository.findAnnotatedObjectByClassAndClassAnnotatedClass(findClazz, findAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
    }

    @DisplayName("annoation class 가 아니면 class 를 찾을 수 없습니다.")
    @Test
    void Given_repository_When_findAnnotatedObjectByClassAndAnnotationClass_with_none_Annotation_class_Then_Exception() throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(Map.of(TestClass.class, new TestClass(),
                                                                                    AnotherTestClass.class, new AnotherTestClass()));

        //when
        Throwable actual = Assertions.catchThrowable(() -> repository.findAnnotatedObjectByClassAndClassAnnotatedClass(TestClass.class, TestClass.class));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("annotated 된 object, method 쌍을 찾아옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindAnnotatedObjectMethodByAnnotationClassTestSuite")
    void Given_repository_When_findAnnotatedObjectMethodByAnnotationClass_Then_foundAnnotatedObjectMethods(Map<Class<?>, Object> repositoryMap,
                                                                                                           Class<?> findAnnotationClazz,
                                                                                                           List<AnnotatedObjectMethod> result) throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(repositoryMap);

        //when
        List<AnnotatedObjectMethod> actuals = repository.findAnnotatedObjectMethodByClassAndMethodAnnotatedClass(findAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
    }

    @DisplayName("annotated 된 object, method 쌍을 찾아옵니다.")
    @ParameterizedTest
    @MethodSource("provideFindAnnotatedObjectMethodByClassAnnotatedClassAndClassMethodAnnotatedClassTestSuite")
    void Given_repository_When_findAnnotatedObjectMethodByClassAnnotatedClassAndClassMethodAnnotatedClass_Then_foundAnnotatedObjectMethods(Map<Class<?>, Object> repositoryMap,
                                                                                                                                           Class<?> findClassAnnotationClazz,
                                                                                                                                           Class<?> findClassMethodAnnotationClazz,
                                                                                                                                           List<AnnotatedObjectMethod> result) throws Exception {
        //given
        AnnotatedObjectRepository repository = new AnnotatedObjectRepository(repositoryMap);

        //when
        List<AnnotatedObjectMethod> actuals = repository.findAnnotatedObjectMethodByClassAnnotatedClassAndMethodAnnotatedClass(findClassAnnotationClazz, findClassMethodAnnotationClazz);

        //then
        Assertions.assertThat(actuals).containsAll(result);
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
                TestClassAnnotation.class,
                List.of(TestClass.class, AnotherTestClass.class)
            ),
            Arguments.of(
                Map.of(TestClass.class, new TestClass(),
                       AnotherTestClass.class, new AnotherTestClass(),
                       NoneAnnotatedClass.class, new NoneAnnotatedClass()),
                TestClassAnnotation.class,
                List.of(TestClass.class, AnotherTestClass.class)
            ),
            Arguments.of(
                Map.of(AnotherTestClass.class, new AnotherTestClass()),
                TestClassAnnotation.class,
                List.of(AnotherTestClass.class)
            ),
            Arguments.of(
                Map.of(TestClass.class, new TestClass()),
                TestClassAnnotation.class,
                List.of(TestClass.class)
            ),
            Arguments.of(
                Map.of(NoneAnnotatedClass.class, new NoneAnnotatedClass()),
                TestClassAnnotation.class,
                Collections.emptyList()
            )
        );
    }

    private static Stream<Arguments> provideFindAnnotatedObjectByAnnotatedClassTestSuite() {
        TestClassAnnotation testClassAnnotation = AnnotationUtils.find(TestClass.class, TestClassAnnotation.class).orElseThrow();
        TestClass testClassObject = new TestClass();
        AnotherTestClass anotherTestClassObject = new AnotherTestClass();
        NoneAnnotatedClass noneAnnotatedClassObject = new NoneAnnotatedClass();

        AnnotatedObject annotatedTestClassObject = new AnnotatedObject(testClassAnnotation, testClassObject);
        AnnotatedObject annotatedAnotherTestClassObject = new AnnotatedObject(testClassAnnotation, anotherTestClassObject);

        return Stream.of(
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject),
                TestClassAnnotation.class,
                List.of(annotatedTestClassObject,
                        annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestClassAnnotation.class,
                List.of(annotatedTestClassObject,
                        annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(AnotherTestClass.class, anotherTestClassObject),
                TestClassAnnotation.class,
                List.of(annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject),
                TestClassAnnotation.class,
                List.of(annotatedTestClassObject)
            ),
            Arguments.of(
                Map.of(NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestClassAnnotation.class,
                Collections.emptyList()
            )
        );
    }

    private static Stream<Arguments> provideFindAnnotatedObjectByClassAndAnnotatedClassTestSuite() {
        TestClassAnnotation testClassAnnotation = AnnotationUtils.find(TestClass.class, TestClassAnnotation.class).orElseThrow();

        TestClass testClassObject = new TestClass();
        AnotherTestClass anotherTestClassObject = new AnotherTestClass();
        NoneAnnotatedClass noneAnnotatedClassObject = new NoneAnnotatedClass();

        AnnotatedObject annotatedTestClassObject = new AnnotatedObject(testClassAnnotation, testClassObject);
        AnnotatedObject annotatedAnotherTestClassObject = new AnnotatedObject(testClassAnnotation, anotherTestClassObject);

        return Stream.of(
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                Object.class,
                TestClassAnnotation.class,
                List.of(annotatedTestClassObject,
                        annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestInterface.class,
                TestClassAnnotation.class,
                List.of(annotatedTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                AnotherTestClass.class,
                TestClassAnnotation.class,
                List.of(annotatedAnotherTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestClass.class,
                TestClassAnnotation.class,
                List.of(annotatedTestClassObject)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                NoneAnnotatedClass.class,
                TestClassAnnotation.class,
                Collections.emptyList()
            )
        );
    }

    private static Stream<Arguments> provideFindAnnotatedObjectMethodByAnnotationClassTestSuite() {
        TestClass testClassObject = new TestClass();
        Method testMethod = TestClass.getTestMethod();
        TestClassMethodAnnotation testClassAnnotation = AnnotationUtils.find(TestClass.class, TestClassMethodAnnotation.class).orElseThrow();
        TestClassMethodAnnotation testMethodAnnotation = AnnotationUtils.find(testMethod, TestClassMethodAnnotation.class).orElseThrow();

        AnnotatedObject annotatedObject = new AnnotatedObject(testClassAnnotation, testClassObject);
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(testMethodAnnotation, testMethod);
        AnnotatedObjectMethod annotatedObjectMethod = new AnnotatedObjectMethod(annotatedObject, annotatedMethod);

        AnotherTestClass anotherTestClassObject = new AnotherTestClass();
        NoneAnnotatedClass noneAnnotatedClassObject = new NoneAnnotatedClass();

        return Stream.of(
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestClassMethodAnnotation.class,
                List.of(annotatedObjectMethod)
            ),
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject),
                TestClassAnnotation.class,
                Collections.emptyList()
            )
        );
    }

    private static Stream<Arguments> provideFindAnnotatedObjectMethodByClassAnnotatedClassAndClassMethodAnnotatedClassTestSuite() {
        TestClass testClassObject = new TestClass();
        Method testMethod = TestClass.getTestMethod();
        TestClassAnnotation testClassAnnotation = AnnotationUtils.find(TestClass.class, TestClassAnnotation.class).orElseThrow();
        TestClassMethodAnnotation testMethodAnnotation = AnnotationUtils.find(testMethod, TestClassMethodAnnotation.class).orElseThrow();

        AnnotatedObject annotatedObject = new AnnotatedObject(testClassAnnotation, testClassObject);
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(testMethodAnnotation, testMethod);
        AnnotatedObjectMethod annotatedObjectMethod = new AnnotatedObjectMethod(annotatedObject, annotatedMethod);

        AnotherTestClass anotherTestClassObject = new AnotherTestClass();
        NoneAnnotatedClass noneAnnotatedClassObject = new NoneAnnotatedClass();
        TestClassOnlyAnnoatedClass testClassOnlyAnnoatedClass = new TestClassOnlyAnnoatedClass();
        TestClassAnnotatedButNotMethodAnnoatedClass testClassAnnotatedButNotMethodAnnoatedClass = new TestClassAnnotatedButNotMethodAnnoatedClass();

        return Stream.of(
            Arguments.of(
                Map.of(TestClass.class, testClassObject,
                       AnotherTestClass.class, anotherTestClassObject,
                       NoneAnnotatedClass.class, noneAnnotatedClassObject,
                       TestClassOnlyAnnoatedClass.class, testClassOnlyAnnoatedClass,
                       TestClassAnnotatedButNotMethodAnnoatedClass.class, testClassAnnotatedButNotMethodAnnoatedClass),
                TestClassAnnotation.class,
                TestClassMethodAnnotation.class,
                List.of(annotatedObjectMethod)
            )
        );
    }


    @Retention(RUNTIME)
    @Target(TYPE)
    private @interface TestClassAnnotation {


    }

    @Retention(RUNTIME)
    @Target({TYPE, METHOD})
    private @interface TestClassMethodAnnotation {

    }

    private interface TestInterface {


    }

    @TestClassMethodAnnotation
    @TestClassAnnotation
    public static class TestClass implements TestInterface {

        @TestClassMethodAnnotation
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

    @TestClassMethodAnnotation
    @TestClassAnnotation
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

    @TestClassAnnotation
    public static class TestClassOnlyAnnoatedClass {

        public void method(int param) {

        }

        public static Method getTestMethod() {
            try {
                return TestClassOnlyAnnoatedClass.class.getMethod("method", int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @TestClassMethodAnnotation
    @TestClassAnnotation
    public static class TestClassAnnotatedButNotMethodAnnoatedClass {

        public void method(int param) {

        }

        public static Method getTestMethod() {
            try {
                return TestClassAnnotatedButNotMethodAnnoatedClass.class.getMethod("method", int.class);
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