package mapper.newsegment.chunk;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;
import mapper.newsegment.chunk.SegmentChunk.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmptySegmentChunkTest {
    @DisplayName("null 입력시 exception 이 발생합니다.")
    @Test
    void test0() throws Exception {
        //given
        EmptySegmentChunk emptySegmentChunk = new EmptySegmentChunk();

        //when
        Throwable actual = Assertions.catchThrowable(() -> emptySegmentChunk.consume(null));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("첫번째 segment 의 값 여부에 따라 결과값을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideSegmentProviderAndExpectResultSize")
    void test1(SegmentProvider provider, int expectResultSize) throws Exception {
        //given
        EmptySegmentChunk emptySegmentChunk = new EmptySegmentChunk();

        //when
        List<Result> actual = emptySegmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(expectResultSize);
    }

    @DisplayName("다음 매칭할 segment 를 반환합니다.")
    @Test
    void test2() throws Exception {
        //given
        Queue<String> queue = new ArrayDeque<>();
        queue.add("");
        queue.add("path");
        SegmentProvider provider = new SegmentProvider(queue);
        EmptySegmentChunk emptySegmentChunk = new EmptySegmentChunk();

        Queue<String> expectQueue = new ArrayDeque<>();
        expectQueue.add("path");
        SegmentProvider expectProvider = new SegmentProvider(expectQueue);

        //when
        List<Result> actual = emptySegmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(1);

        Result actualResult = actual.get(0);
        SegmentProvider actualProvider = actualResult.getLeftSegments();

        Assertions.assertThat(actualProvider).isEqualTo(expectProvider);
    }

    @DisplayName("비어있는 matchSegment 를 반환합니다.")
    @Test
    void test() throws Exception {
        //given
        Queue<String> queue = new ArrayDeque<>();
        queue.add("");
        queue.add("path");
        SegmentProvider provider = new SegmentProvider(queue);
        EmptySegmentChunk emptySegmentChunk = new EmptySegmentChunk();

        MatchSegment expectMatchSegment = MatchSegment.empty();

        //when
        List<Result> actual = emptySegmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(1);

        Result actualResult = actual.get(0);
        MatchSegment matchSegment = actualResult.getMatchSegment();

        Assertions.assertThat(matchSegment).isEqualTo(expectMatchSegment);
    }

    public static Stream<Arguments> provideSegmentProviderAndExpectResultSize() {
        Queue<String> successQueue = new ArrayDeque<>();
        successQueue.add("");
        successQueue.add("path");
        SegmentProvider successProvider = new SegmentProvider(successQueue);

        Queue<String> failQueue = new ArrayDeque<>();
        failQueue.add("path");
        failQueue.add("");
        SegmentProvider failProvider = new SegmentProvider(failQueue);

        return Stream.of(
            Arguments.of(successProvider, 1),
            Arguments.of(failProvider, 0)
        );
    }
}