//package mapper;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Parameter;
//import java.text.MessageFormat;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import vo.HttpMethod;
//
//class TaskActuatorTest {
//
//    @DisplayName("taskIndicator 가 일치하면 true 를 반환합니다.")
//    @Test
//    void test1_1() throws Exception {
//        //given
//        TaskIndicator taskIndicator = new TaskIndicator(HttpMethod.GET, "/test");
//        TestTaskActuator testTaskActuator = new TestTaskActuator(taskIndicator, TestTask.class, TestTask.getMethod());
//
//        TaskIndicator otherIndicator = new TaskIndicator(HttpMethod.GET, "/test");
//
//        //when
//        boolean actual = testTaskActuator.isIndicated(otherIndicator);
//
//        //then
//        Assertions.assertThat(actual).isTrue();
//    }
//
//    @DisplayName("taskIndicator 가 일치하지 않으면 false 를 반환합니다.")
//    @Test
//    void test1_2() throws Exception {
//        //given
//        TaskIndicator taskIndicator = new TaskIndicator(HttpMethod.GET, "/test");
//        TestTaskActuator testTaskActuator = new TestTaskActuator(taskIndicator, TestTask.class, TestTask.getMethod());
//
//        TaskIndicator otherIndicator = new TaskIndicator(HttpMethod.GET, "/diffUrlTest");
//
//        //when
//        boolean actual = testTaskActuator.isIndicated(otherIndicator);
//
//        //then
//        Assertions.assertThat(actual).isFalse();
//    }
//
//    @DisplayName("method 호출에 필요한 parameter 를 반환합니다.")
//    @Test
//    void test2_1() throws Exception {
//        //given
//        TaskIndicator taskIndicator = new TaskIndicator(HttpMethod.GET, "/test");
//        TestTaskActuator testTaskActuator = new TestTaskActuator(taskIndicator, TestTask.class, TestTask.getMethod());
//
//        //when
//        Parameter[] actual = testTaskActuator.getParameters();
//
//        //then
//        Assertions.assertThat(actual).isNotNull();
//    }
//
//    @DisplayName("method 를 실행합니다.")
//    @Test
//    void test2_2() throws Exception {
//        //given
//        TaskIndicator taskIndicator = new TaskIndicator(HttpMethod.GET, "/test");
//        TestTaskActuator testTaskActuator = new TestTaskActuator(taskIndicator, TestTask.class, TestTask.getMethod());
//
//        Parameter[] actual = testTaskActuator.getParameters();
//
//        //when
//        Object object = testTaskActuator.execute(new Object[]{1});
//
//        //then
//        Assertions.assertThat(object).isNotNull();
//    }
//
//
//    @DisplayName("")
//    @Test
//    void test2() throws Exception {
////        //given
////        Class<TestTaskActuator> taskClass = TestTaskActuator.class;
////        Method sumMethod = TestTaskActuator.getMethod();
////
////        for (Parameter parameter : sumMethod.getParameters()) {
////            System.out.println(parameter.getType());
////
////            System.out.println(parameter);
////        }
////
////        TestTaskActuator testTaskActuator = taskClass.getConstructor(int.class).newInstance(3);
////
////        int invoke = (int) sumMethod.invoke(testTaskActuator, 4);
////
////        System.out.println(invoke);
////
//////        System.out.println(MessageFormat.format("parameters : {}", s));
////
//////        Object invoke = method.invoke(null);
////
////        //when
////
////        //then
//
//    }
//
//    public static class TestTaskActuator<T> {
//        private final TaskIndicator taskIndicator;
//
//        private final Object object;
//        private final Method method;
//
//        public TestTaskActuator(TaskIndicator taskIndicator, Object object, Method method) {
//            this.taskIndicator = taskIndicator;
//            this.object = object;
//            this.method = method;
//        }
//
//        public boolean isIndicated(TaskIndicator otherIndicator) {
//            return taskIndicator.equals(otherIndicator);
//        }
//
//        public Parameter[] getParameters() {
//            return method.getParameters();
//        }
//
//        // TODO - controller class 들은 미리 bean 으로 만들어 두자.
//        // 여기서 생성하려니 너무 어렵다.
//        public T execute(Object[] objects) {a
//            try {
//                Class<?> returnType = method.getReturnType();
//
//                Object invoke = method.invoke(objects, objects);
//
//                return (T)invoke;
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            } catch (InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//    }
//
//    public static class TestTask {
//        private final int num;
//        private final String string;
//
//        public TestTask(int num, String string) {
//            this.num = num;
//            this.string = string;
//        }
//
//        public int testSum(int a) {
//            int result = 3 + a;
//            System.out.println(MessageFormat.format("sum : {0}", result));
//            return result;
//        }
//
//        public static Method getMethod() {
//            try {
//                return TestTask.class.getDeclaredMethod("testSum", int.class);
//            } catch (NoSuchMethodException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
//
