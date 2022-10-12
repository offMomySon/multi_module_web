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
public class Config {
    private static final String path = "http/src/main/resources/config/http_config.json";
    public static final Config instance = create();

    private final int port;
    private final String welcomePage;

    private final int maxConnection;
    private final int waitConnection;

    private final String resourceRootPath;

    private final AddressDownloadRate baseDownloadRate;
    private final AddressRestrictFileExtension baseRestrictFileExtension;

    private final Set<AddressDownloadRate> specificDownloadRate;
    private final Set<AddressRestrictFileExtension> specificRestrictFileExtension;

    public Config(int port, String welcomePage, int maxConnection, int waitConnection, String resourceRootPath, AddressDownloadRate baseDownloadRate,
                  AddressRestrictFileExtension baseRestrictFileExtension, Set<AddressDownloadRate> specificDownloadRate, Set<AddressRestrictFileExtension> specificRestrictFileExtension) {
        this.port = port;
        this.welcomePage = welcomePage;
        this.maxConnection = maxConnection;
        this.waitConnection = waitConnection;
        this.resourceRootPath = resourceRootPath;
        this.baseDownloadRate = baseDownloadRate;
        this.baseRestrictFileExtension = baseRestrictFileExtension;
        this.specificDownloadRate = specificDownloadRate;
        this.specificRestrictFileExtension = specificRestrictFileExtension;
    }

    @JsonCreator
    private static Config ofJackSon(@JsonProperty("port") int port,
                                    @JsonProperty("welcomePage") String welcomePage,
                                    @JsonProperty("maxConnection") int maxConnection,
                                    @JsonProperty("waitConnection") int waitConnection,
                                    @JsonProperty("resource") String resourceRootPath,
                                    @JsonProperty("baseDownloadRate") AddressDownloadRate baseDownloadRate,
                                    @JsonProperty("baseRestrictFileExtension") AddressRestrictFileExtension baseRestrictFileExtension,

                                    @JsonProperty("specificIpDownloadRate") Set<AddressDownloadRate> specificIpDownloadRate,
                                    @JsonProperty("specificIpRestrictFileExtension") Set<AddressRestrictFileExtension> specificIpRestrictFileExtensions) {
        return new Config(port, welcomePage, maxConnection, waitConnection, resourceRootPath, baseDownloadRate, baseRestrictFileExtension, specificIpDownloadRate, specificIpRestrictFileExtensions);
    }

    private static Config create() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(path), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
