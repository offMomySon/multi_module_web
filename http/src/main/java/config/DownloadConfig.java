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

    private final String root;
    private final Set<String> restrictFileExtension;
    private final DownloadsRate downloadsRate;
    private final

    private DownloadConfig(String root, DownloadsRate downloadsRate, Set<String> restrictFileExtension) {
        this.root = root;
        this.downloadsRate = downloadsRate;
        this.restrictFileExtension = restrictFileExtension;
    }

    @JsonCreator
    private static DownloadConfig ofJackSon(@JsonProperty("root") String root,
                                            @JsonProperty("period") DownloadsRate downloadsRate,
                                            @JsonProperty("restrictFileExtension") Set<String> restrictFileExtension) {

        return new DownloadConfig(root, downloadsRate, restrictFileExtension);
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
