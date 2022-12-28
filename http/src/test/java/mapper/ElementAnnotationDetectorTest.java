package mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ElementAnnotationDetectorTest {

    @DisplayName("element 에 존재하는 annotation 중에서 특정 annotation 의 존재여부를 확인한다.")
    @Test
    void test1_1() throws Exception {
        //given
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(new AnnotatedClass(TestAnnotatedClass.class));

        //when
        boolean actual = elementAnnotationDetector.isAnnotated(TestAnnotation.class);

        //then
        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("eleemnt 에 존재하는 annotation 중에서 특정 annotation 을 존재여부를 확인할 때, annotation class 가 아니면 false 를 반환합니다.")
    @Test
    void test1_2() throws Exception {
        //given
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(new AnnotatedClass(TestAnnotatedClass.class));

        //when
        boolean actual = elementAnnotationDetector.isAnnotated(TestAnnotatedClass.class);

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("element 에 존재하는 annotation 중에서 특정 annotation 찾습니다.")
    @Test
    void test2_1() throws Exception {
        //given
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(new AnnotatedClass(TestAnnotatedClass.class));

        //when
        Optional<TestAnnotation> actual = elementAnnotationDetector.find(TestAnnotation.class);

        //then
        Assertions.assertThat(actual).isPresent();
    }

    // TODO - 다양한 class 를 테스트 하고 싶으면?
    @DisplayName("eleement 에 존재하는 annotatino 중에서 특정 annotaion 을 찾을때, annotation class 를 인자로 전달하지 않으면 empty 를 반환합니다.")
    @Test
    void test2_2() throws Exception {
        //given
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(new AnnotatedClass(TestAnnotatedClass.class));

        //when
        Optional<TestAnnotatedClass> actual = elementAnnotationDetector.find(TestAnnotatedClass.class);

        //then
        Assertions.assertThat(actual).isEmpty();
    }


    // TODO - true, false 케이스로 test 를 나눠야하는가?
    // 결과를 포함하게되어 복잡한 test suit 를 운용하는 것 보다는, 단순한 테스트 인자만을 전달해주는 test suit 가 좋다고 생각한다.
    // true, false 를 하나의 테스트로 묶으면 경우의 수가 2개로 나뉘기 때문에,
    // 테스트 suit 안의 각각의 케이스마다 결과값을 포함할 수 밖에없다.
    // 그렇다면 test suit 는 순수한 테스트 케이스를 포함하는것이 아니라 결과값을 포함한 test suit 가 된다.
    @DisplayName("element 의 하위 element 가 존재하면 true 를 반환합니다.")
    @Test
    void test3_1() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestAnnotatedClass.class);
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(annotatedClass);

        //when
        boolean actual = elementAnnotationDetector.hasSubElement();

        //then
        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("element 의 하위 element 가 존재하지 않으면 false 를 반환합니다.")
    @Test
    void test3_2() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestAnnotatedClass.getAnnotatedMethod());
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(annotatedMethod);

        //when
        boolean actual = elementAnnotationDetector.hasSubElement();

        //then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("element 의 하위 element 중 특정 annotation 을 가진 element 를 찾습니다")
    @Test
    void test4_1() throws Exception {
        //given
        AnnotatedClass annotatedClass = new AnnotatedClass(TestAnnotatedClass.class);
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(annotatedClass);

        //when
        List<AnnotatedElement> actual = elementAnnotationDetector.findAnnotatedElementOnSubElement(TestAnnotation.class);

        //then
        Assertions.assertThat(actual).isNotEmpty();
    }


    @DisplayName("element 의 하위 element 가 존재 하지 않으면 empty 를 반환합니다.")
    @Test
    void test4_2() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestAnnotatedClass.getAnnotatedMethod());
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(annotatedMethod);

        //when
        List<AnnotatedElement> actual = elementAnnotationDetector.findAnnotatedElementOnSubElement(TestAnnotation.class);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @DisplayName("element 의 하위 element 중 특정 annotation 을 가진 element 를 찾을때, annotation class 를 인자로 전달하지 않으면 empty 를 반환합니다.")
    @Test
    void test4_3() throws Exception {
        //given
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(TestAnnotatedClass.getAnnotatedMethod());
        ElementAnnotationDetector elementAnnotationDetector = new ElementAnnotationDetector(annotatedMethod);

        //when
        List<AnnotatedElement> actual = elementAnnotationDetector.findAnnotatedElementOnSubElement(TestAnnotatedClass.class);

        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @TestAnnotation
    public static class TestAnnotatedClass {

        @TestAnnotation
        public void annotatedMethod() {

        }

        public void doesNotAnnotatedMethod() {

        }

        public static Method getAnnotatedMethod() {
            try {
                return TestAnnotatedClass.class.getDeclaredMethod("annotatedMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {

    }


}