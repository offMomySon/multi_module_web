//package mapper;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//class JackSontest {
//
//    @DisplayName("")
//    @Test
//    void test() throws Exception {
//        //given
//        JsonMapper jsonMapper = new JsonMapper();
//
//        Object object = new Person();
//        String json = jsonMapper.writeValueAsString(object);
//        System.out.println(json);
//        System.out.println(object.getClass());
//
//
//        //when
//
//        //then
//
//    }
//
//    public static class Person {
//        @JsonProperty("job")
//        public String getName2() {
//            return "developer";
//        }
//
//        public String getName(){
//            return "jihun";
//        }
//    }
//
//}