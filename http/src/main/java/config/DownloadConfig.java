package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class DownloadConfig {
    private static final String path = "http/src/main/resources/config/download_config.json";
    public static final DownloadConfig instance = create();

    private final String rootFilePath;
    private final DownloadSetting baseDownloadSetting;
    private final Set<DownloadSetting> specificIpDownloadSetting;

    public DownloadConfig(String rootFilePath, DownloadSetting baseDownloadSetting, Set<DownloadSetting> specificIpDownloadSetting) {
        this.rootFilePath = rootFilePath;
        this.baseDownloadSetting = baseDownloadSetting;
        this.specificIpDownloadSetting = specificIpDownloadSetting;

        log.info("rootFilePath : `{}`", rootFilePath);
        log.info("baseDownloadSetting : `{}`", baseDownloadSetting);
        log.info("specificIpDownloadSetting : `{}`", specificIpDownloadSetting);
    }

    @JsonCreator
    private static DownloadConfig ofJackSon(@JsonProperty("root") String root,
                                            @JsonProperty("base") DownloadSetting baseDownloadSetting,
                                            @JsonProperty("specificIp") Set<DownloadSetting> specificIpDownloadSetting) {
        return new DownloadConfig(root, baseDownloadSetting, specificIpDownloadSetting);
    }

    private static DownloadConfig create() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(path), DownloadConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
