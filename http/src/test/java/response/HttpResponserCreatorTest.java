package response;

import dto.ResponseStatus;
import io.IoUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import validate.ValidateUtil;
import static java.nio.charset.StandardCharsets.UTF_8;

class HttpResponserCreatorTest {
    private static final String END_OF_LINE = "\r\n";
    private static final String KEY_VALUE_DELIMITER = ":";
    private static final String VALUE_DELIMITER = ", ";

    private static final byte[] BUFFER = new byte[8192];

    private static final String TEST_IMAGE_PATH = "/testJPG.jpg";

    @DisplayName("입력한 startLine, header, body 를 http 포멧 메세지를 출력합니다.")
    @ParameterizedTest
    @MethodSource("provideStatusAndHeaderAndBody")
    void test(ResponseStatus responseStatus, Map<String, Set<String>> header, String body) {
        // given
        String responseLine = responseStatus.getStatusLine();
        ByteArrayInputStream bodyInputStream = new ByteArrayInputStream(body.getBytes(UTF_8));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponser httpResponser = new HttpResponser(outputStream);
        httpResponser
            .responseStatus(responseLine)
            .header(header)
            .body(bodyInputStream);

        // when
        httpResponser.send();
        String actual = outputStream.toString(UTF_8);

        // then
        String expect = generateExpectMessage(responseLine, header, body);

        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("입력한 startLine, header, body 중 null 이 존재하면, null 을 제외한 http 메세지를 출력합니다.")
    @ParameterizedTest
    @MethodSource("provideHeaderElementsWithNull")
    void test2(ResponseStatus responseStatus, Map<String, Set<String>> header, String body) {
        // given
        String responseLine = Objects.isNull(responseStatus) ? null : responseStatus.getStatusLine();
        ByteArrayInputStream bodyInputStream = Objects.isNull(body) ? null : new ByteArrayInputStream(body.getBytes(UTF_8));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponser httpResponser = new HttpResponser(outputStream);
        httpResponser
            .responseStatus(responseLine)
            .header(header)
            .body(bodyInputStream);

        // when
        httpResponser.send();
        String actual = outputStream.toString(UTF_8);

        // then
        String expect = generateExpectMessage(responseLine, header, body);

        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("바이너리 형태의 파일을 http response 형식으로 body 에 담아 전달해야 합니다.")
    @ParameterizedTest
    @MethodSource("provideBinaryFileBytes")
    void test3(byte[] resourceBytes) {
        // given
        String responseLine = ResponseStatus.OK.getStatusLine();
        Map<String, Set<String>> header = Map.of("content", Set.of("test/random"));
        ByteArrayInputStream bodyInputStream = new ByteArrayInputStream(resourceBytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponser httpResponser = new HttpResponser(outputStream);
        httpResponser
            .responseStatus(responseLine)
            .header(header)
            .body(bodyInputStream);

        // when
        httpResponser.send();
        byte[] actual = outputStream.toByteArray();

        // then
        byte[] expect = generateExpectMessage(responseLine, header, new ByteArrayInputStream(resourceBytes));

        Assertions.assertThat(actual).isEqualTo(expect);
    }

    private static String generateExpectMessage(String responseLine, Map<String, Set<String>> header, String body) {
        StringBuilder responseBuilder = new StringBuilder();
        if (Objects.nonNull(responseLine)) {
            responseBuilder.append(responseLine).append(END_OF_LINE);
        }
        if (Objects.nonNull(header)) {
            responseBuilder.append(generateHeaderMessage(header));
        }

        responseBuilder.append(END_OF_LINE);

        if (ValidateUtil.isValid(body)) {
            responseBuilder.append(body);
        }

        return responseBuilder.toString();
    }

    private static byte[] generateExpectMessage(String responseLine, Map<String, Set<String>> header, InputStream bodyInputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedWriter bufferedWriter = IoUtils.createBufferedWriter(outputStream);
        BufferedOutputStream bufferedOutputStream = IoUtils.createBufferedOutputStream(outputStream);

        try {
            if (ValidateUtil.isValid(responseLine)) {
                bufferedWriter.write(responseLine + END_OF_LINE);
            }
            if (Objects.nonNull(header)) {
                bufferedWriter.write(generateHeaderMessage(header));
            }

            bufferedWriter.write(END_OF_LINE);
            bufferedWriter.flush();

            if (Objects.nonNull(bodyInputStream)) {
                while (bodyInputStream.available() != 0) {
                    int read = bodyInputStream.read(BUFFER);
                    bufferedOutputStream.write(BUFFER, 0, read);
                }
            }

            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }

    private static String generateHeaderMessage(Map<String, Set<String>> headers) {
        StringBuilder headerBuilder = new StringBuilder();

        for (String key : headers.keySet()) {
            String value = String.join(VALUE_DELIMITER, headers.get(key));
            headerBuilder.append(key).append(KEY_VALUE_DELIMITER).append(value).append(END_OF_LINE);
        }

        return headerBuilder.toString();
    }

    private static byte[] getResource(String path) {
        InputStream resourceAsStream = HttpResponserCreatorTest.class.getResourceAsStream(path);
        try {
            return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Arguments> provideStatusAndHeaderAndBody() {
        return Stream.of(
            Arguments.of(ResponseStatus.OK, Map.of("content", Set.of("jpg/image"), "testHedaer", Set.of("11", "22")), "test body"),
            Arguments.of(ResponseStatus.OK, Map.of("content", Set.of("html/text"), "content-Length", Set.of("10"), "testHedaer", Set.of("11", "22")), "test body2"),
            Arguments.of(ResponseStatus.OK, Map.of("content", Set.of("sdf"), "testHedaer", Set.of("11", "22")), "test body3")
        );
    }

    private static Stream<Arguments> provideHeaderElementsWithNull() {
        return Stream.of(
            Arguments.of(null, Map.of("content", Set.of("jpg/image"), "testHedaer", Set.of("11", "22")), "test body1"),
            Arguments.of(ResponseStatus.OK, null, "test body1"),
            Arguments.of(ResponseStatus.OK, Map.of("content", Set.of("sdf"), "testHedaer", Set.of("11", "22")), null),
            Arguments.of(ResponseStatus.OK, Map.of("content", Set.of("sdf"), "testHedaer", Set.of("11", "22")), ""),
            Arguments.of(ResponseStatus.OK, null, null),
            Arguments.of(null, Map.of("content", Set.of("sdf"), "testHedaer", Set.of("11", "22")), null),
            Arguments.of(null, null, "test body1")
        );
    }

    private static Stream<Arguments> provideBinaryFileBytes() {
        return Stream.of(
            Arguments.of(getResource("/testJPG.jpg")),
            Arguments.of(getResource("/testMP4.mp4")),
            Arguments.of(getResource("/testPOM.pom"))
        );
    }
}