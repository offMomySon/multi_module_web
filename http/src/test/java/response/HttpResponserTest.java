package response;

import dto.ResponseStatus;
import io.IoUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static validate.ValidateUtil.isValid;

class HttpResponserTest {
    private static final String END_OF_LINE = "\r\n";

    private static final byte[] BUFFER = new byte[8192];

    @DisplayName("입력한 startLine, header, body 를 http 포멧 메세지를 출력합니다.")
    @ParameterizedTest
    @MethodSource("provideStatusAndHeaderAndBody")
    void test(String responseLine, String header, byte[] resourceBytes) {
        // given
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

    @DisplayName("입력한 startLine, header, body 중 null 이 존재하면, null 을 제외한 http 메세지를 출력합니다.")
    @ParameterizedTest
    @MethodSource("provideHeaderElementsWithNull")
    void test2(String responseLine, String header, byte[] resourceBytes) {
        // given
        ByteArrayInputStream bodyInputStream = Objects.isNull(resourceBytes) || resourceBytes.length == 0 ? null : new ByteArrayInputStream(resourceBytes);
        ByteArrayInputStream resourceInputStream = Objects.isNull(resourceBytes) || resourceBytes.length == 0 ? null : new ByteArrayInputStream(resourceBytes);

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
        byte[] expect = generateExpectMessage(responseLine, header, resourceInputStream);

        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("바이너리 형태의 파일을 http response 형식으로 body 에 담아 전달해야 합니다.")
    @ParameterizedTest
    @MethodSource("provideBinaryFileBytes")
    void test3(byte[] resourceBytes) {
        // given
        String responseLine = ResponseStatus.OK.getStatusLine();
        String header = "content : video/mp4\r\ncontent-Length : 19999\r\ntestHeader : 11 ; 22\r\n";
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

    private static byte[] generateExpectMessage(String responseLine, String header, InputStream bodyInputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedWriter bufferedWriter = IoUtils.createBufferedWriter(outputStream);
        BufferedOutputStream bufferedOutputStream = IoUtils.createBufferedOutputStream(outputStream);

        try {
            if (isValid(responseLine)) {
                bufferedWriter.write(responseLine + END_OF_LINE);
            }
            if (isValid(header)) {
                bufferedWriter.write(header + END_OF_LINE);
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

    private static Stream<Arguments> provideStatusAndHeaderAndBody() {
        return Stream.of(
            Arguments.of(ResponseStatus.OK.getStatusLine(), "content : jpg/image\r\ntestHeader : 11 ; 22\r\n", "test body".getBytes(UTF_8)),
            Arguments.of(ResponseStatus.OK.getStatusLine(), "content : jpg/image\r\ncontent-Length : 10\r\ntestHeader : 11 ; 22\r\n", "test body2".getBytes(UTF_8)),
            Arguments.of(ResponseStatus.OK.getStatusLine(), "content : video/mp4\r\ncontent-Length : 19999\r\ntestHeader : 11 ; 22\r\n", "test body3".getBytes(UTF_8))
        );
    }

    private static Stream<Arguments> provideHeaderElementsWithNull() {
        return Stream.of(
            Arguments.of(ResponseStatus.OK.getStatusLine(), "content : sdf\r\ntestHedaer : 11;22\r\n", "".getBytes(UTF_8)),
            Arguments.of(ResponseStatus.OK.getStatusLine(), "content : sdf", null),
            Arguments.of(ResponseStatus.OK.getStatusLine(), null, "test body1".getBytes(UTF_8)),
            Arguments.of(ResponseStatus.OK.getStatusLine(), null, null),
            Arguments.of(null, "content : jpg/image\r\n testHedaer : 11, 22\r\n", "test body1".getBytes(UTF_8)),
            Arguments.of(null, "content : sdf\r\ntestHedaer : 11;22\r\n", null),
            Arguments.of(null, null, "test body1".getBytes(UTF_8)),
            Arguments.of(null, null, null)
        );
    }

    private static Stream<Arguments> provideBinaryFileBytes() {
        return Stream.of(
            Arguments.of(getResource("/testJPG.jpg")),
            Arguments.of(getResource("/testMP4.mp4")),
            Arguments.of(getResource("/testPOM.pom"))
        );
    }

    private static byte[] getResource(String path) {
        InputStream resourceAsStream = HttpResponserTest.class.getResourceAsStream(path);
        try {
            return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}