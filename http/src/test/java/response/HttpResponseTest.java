package response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vo.HttpResponse;
import vo.ResponseStatus;
import static java.nio.charset.StandardCharsets.UTF_8;

// 테스트는 입력에대한 출력과 예상되는 출력의 동일성을 확인하는 과정이다.
// HttpResponser 는 responseLine, header, body 을 입력으로 httpResponse message 를 출력하는것이다.

// 테스트에서 예상 출력을 만들어야한다.
// 이떄 예상 출력을 만들기 위해서는 테스트 코드에서 생성하는 방법과, 테스트시트에 수작업으로 미리 만들어두는 방법이 있을것 같다.
// 첫번째 방법은 HttpResonser 의 역할을 하는 코드를 테스트 코드에서 다시 만드는것이기 때문에 테스트 의미가 없어진다. 왜냐하면 똑같은 로직의 코드의 결과를 비교하는 것이기 때문이다.
// 그렇다면 두번째 방법은, 비록 수작업이 필요하기 때문에 많은 시간이 소요되지만, HttpResonser 의 코드 동작결과를 목표로하는 결과와 매핑시키는 것이기 때문에 유효하다.
class HttpResponseTest {
    private static final String END_OF_LINE = "\r\n";

    private static final byte[] BUFFER = new byte[8192];

    @DisplayName("입력한 startLine, header, body 를 http 포멧 메세지를 출력합니다.")
    @ParameterizedTest
    @MethodSource("provideStatusAndHeaderAndBody")
    void test(String responseLine, String header, byte[] resourceBytes, String expectMessage) {
        // given
        ByteArrayInputStream bodyInputStream = new ByteArrayInputStream(resourceBytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);
        httpResponse
            .status(responseLine)
            .header(header)
            .body(bodyInputStream);

        // when
        httpResponse.send();
        byte[] actual = outputStream.toByteArray();

        // then
        byte[] expect = expectMessage.getBytes();
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("입력한 startLine, header, body 중 null 이 존재하면, null 을 제외한 http 메세지를 출력합니다.")
    @ParameterizedTest
    @MethodSource("provideHeaderElementsWithNull")
    void test2(String responseLine, String header, byte[] resourceBytes, String expectMessage) {
        // given
        ByteArrayInputStream bodyInputStream = Objects.isNull(resourceBytes) || resourceBytes.length == 0 ? null : new ByteArrayInputStream(resourceBytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);
        httpResponse
            .status(responseLine)
            .header(header)
            .body(bodyInputStream);

        // when
        httpResponse.send();
        byte[] actual = outputStream.toByteArray();

        // then
        byte[] expect = expectMessage.getBytes(UTF_8);
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("바이너리 형태의 파일을 http response 형식으로 body 에 담아 전달해야 합니다.")
    @ParameterizedTest
    @MethodSource("provideBinaryFileBytes")
    void test3(String responseLine, String header, byte[] resourceBytes, byte[] expect) {
        // given
        ByteArrayInputStream bodyInputStream = new ByteArrayInputStream(resourceBytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);
        httpResponse
            .status(responseLine)
            .header(header)
            .body(bodyInputStream);

        // when
        httpResponse.send();
        byte[] actual = outputStream.toByteArray();

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    private static Stream<Arguments> provideStatusAndHeaderAndBody() {
        return Stream.of(
            Arguments.of(ResponseStatus.OK.getStatusLine(),
                         "content : jpg/image\r\ntestHeader : 11 ; 22\r\n", "test body".getBytes(UTF_8),
                         "HTTP/1.1 200 OK\r\ncontent : jpg/image\r\ntestHeader : 11 ; 22\r\n\r\ntest body"
            ),
            Arguments.of(ResponseStatus.OK.getStatusLine(),
                         "content : jpg/image\r\ncontent-Length : 10\r\ntestHeader : 11 ; 22\r\n", "test body2".getBytes(UTF_8),
                         "HTTP/1.1 200 OK\r\ncontent : jpg/image\r\ncontent-Length : 10\r\ntestHeader : 11 ; 22\r\n\r\ntest body2"
            ),
            Arguments.of(ResponseStatus.OK.getStatusLine(),
                         "content : video/mp4\r\ncontent-Length : 19999\r\ntestHeader : 11 ; 22\r\n", "test body3".getBytes(UTF_8),
                         "HTTP/1.1 200 OK\r\ncontent : video/mp4\r\ncontent-Length : 19999\r\ntestHeader : 11 ; 22\r\n\r\ntest body3")
        );
    }

    private static Stream<Arguments> provideHeaderElementsWithNull() {
        return Stream.of(
            Arguments.of(ResponseStatus.OK.getStatusLine(), "content : sdf\r\ntestHedaer : 11;22\r\n", "".getBytes(UTF_8),
                         "HTTP/1.1 200 OK\r\ncontent : sdf\r\ntestHedaer : 11;22\r\n\r\n"),
            Arguments.of(ResponseStatus.OK.getStatusLine(), "content : sdf\r\n", null,
                         "HTTP/1.1 200 OK\r\ncontent : sdf\r\n\r\n"),
            Arguments.of(ResponseStatus.OK.getStatusLine(), null, "test body1".getBytes(UTF_8),
                         "HTTP/1.1 200 OK\r\n\r\ntest body1"),
            Arguments.of(ResponseStatus.OK.getStatusLine(), null, null,
                         "HTTP/1.1 200 OK\r\n\r\n"),
            Arguments.of(null, "content : jpg/image\r\ntestHedaer : 11, 22\r\n", "test body1".getBytes(UTF_8),
                         "content : jpg/image\r\ntestHedaer : 11, 22\r\n\r\ntest body1"),
            Arguments.of(null, "content : sdf\r\ntestHedaer : 11;22\r\n", null,
                         "content : sdf\r\ntestHedaer : 11;22\r\n\r\n"),
            Arguments.of(null, null, "test body1".getBytes(UTF_8),
                         "\r\ntest body1"),
            Arguments.of(null, null, null,
                         "\r\n")
        );
    }

    private static Stream<Arguments> provideBinaryFileBytes() {
        return Stream.of(
            Arguments.of(ResponseStatus.OK.getStatusLine(),
                         "content : jpg/image\r\ntestHeader : 11 ; 22\r\n",
                         getResource("/testJPG.jpg"),
                         combine("HTTP/1.1 200 OK\r\ncontent : jpg/image\r\ntestHeader : 11 ; 22\r\n\r\n".getBytes(UTF_8), getResource("/testJPG.jpg"))
            ),
            Arguments.of(ResponseStatus.OK.getStatusLine(),
                         "content : jpg/image\r\ntestHeader : 11 ; 22\r\n",
                         getResource("/testJPG.jpg"),
                         combine("HTTP/1.1 200 OK\r\ncontent : jpg/image\r\ntestHeader : 11 ; 22\r\n\r\n".getBytes(UTF_8), getResource("/testJPG.jpg"))
            ),
            Arguments.of(ResponseStatus.OK.getStatusLine(),
                         "content : jpg/image\r\ntestHeader : 11 ; 22\r\n",
                         getResource("/testJPG.jpg"),
                         combine("HTTP/1.1 200 OK\r\ncontent : jpg/image\r\ntestHeader : 11 ; 22\r\n\r\n".getBytes(UTF_8), getResource("/testJPG.jpg"))
            )
        );
    }

    private static byte[] getResource(String path) {
        InputStream resourceAsStream = HttpResponseTest.class.getResourceAsStream(path);
        try {
            return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] combine(byte[]... sources) {
        int totalSourceLength = Arrays.stream(sources).mapToInt(s -> s.length).sum();
        byte[] allBytes = new byte[totalSourceLength];

        ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes);
        for(byte[] source : sources){
            byteBuffer.put(source);
        }

        return byteBuffer.array();
    }
}