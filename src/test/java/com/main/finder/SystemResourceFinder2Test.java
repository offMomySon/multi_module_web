package com.main.finder;

import java.nio.file.Path;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SystemResourceFinder2Test {
    // todo
    // 테스트 디렉토리를 어떻게 참조하는가? (ex. test/java/resources)
    // build 시, 위 directory 가 포함되지 않는다.
    @DisplayName("정적 파일의 참조 여부에 따라 값을 반환합니다.")
    @ParameterizedTest
    @CsvSource(value = {
        "../../resources/main, config.json, true",
        "../../resources/main, nonExist.json, false",
        "../../resources/main, nonExistJpg.json, false"
    })
    void Given_resourceFinder_When_findFile_Then_present_result(String resourcePackage, String file, boolean expect) throws Exception {
        //given
        String prefix = "/static";
        SystemResourceFinder2 resourceFinder = SystemResourceFinder2.fromPackage(this.getClass(), resourcePackage, prefix);
        Path filePath = Path.of(prefix + "/" + file);

        //when
        Optional<Path> optionalResource = resourceFinder.findFile(filePath);

        //then
        Assertions.assertThat(optionalResource.isPresent()).isEqualTo(expect);
    }
}