package converter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileConverterTest {


    @DisplayName("")
    @Test
    void test() throws Exception {
        //given
        TestClass testClass = new TestClass("v1", "v2", 3);
        String testString = "123";
        Integer testInteger = 1;
        int testInt = 1;
        double testDouble = 1.1f;
        TestEnum testEnum = TestEnum.AA;

        Converter converter = new ObjectConverter();

        //when
        InputStream inputStream = converter.convertToInputStream(testEnum);

        byte[] bytes = inputStream.readAllBytes();
        String s = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(s);
        //then

    }

    public static class TestClass {
        private final String value;
        private final String value2;
        private final int value3;

        public TestClass(String value, String value2, int value3) {
            this.value = value;
            this.value2 = value2;
            this.value3 = value3;
        }

        public String getValue() {
            return value;
        }

        public String getValue2() {
            return value2;
        }

        public int getValue3() {
            return value3;
        }
    }

    public enum TestEnum {
        AA(1);

        private final int n;

        TestEnum(int i) {
            n = i;
        }

        public int getN() {
            return n;
        }
    }

}

