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
public class HttpConfig {
    private static final String path = "http/src/main/resources/config/http_config.json";
    public static final HttpConfig instance = create();

    private final int port;
    private final String welcomePage;

    private final int maxConnection;
    private final int waitConnection;

    private final String resourceRootPath;

    private final DownloadRate baseDownloadRate;
    private final Set<FileExtension> baseRestrictFileExtension;

    private final Set<IpAddressDownloadRate> specificDownloadRate;
    private final Set<IpAddressRestrictFileExtension> specificRestrictFileExtension;

    public HttpConfig(int port, String welcomePage, int maxConnection, int waitConnection, String resourceRootPath, DownloadRate baseDownloadRate,
                      Set<FileExtension> baseRestrictFileExtension, Set<IpAddressDownloadRate> specificDownloadRate, Set<IpAddressRestrictFileExtension> specificRestrictFileExtension) {
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
    private static HttpConfig ofJackSon(@JsonProperty("port") int port,
                                        @JsonProperty("welcomePage") String welcomePage,
                                        @JsonProperty("maxConnection") int maxConnection,
                                        @JsonProperty("waitConnection") int waitConnection,
                                        @JsonProperty("resource") String rootDDirectoryPath,
                                        @JsonProperty("downloadRate") DownloadRate baseDownloadRate,
                                        @JsonProperty("restrictFileExtension") Set<FileExtension> baseRestrictFileExtension,
                                        @JsonProperty("specificIpDownloadRate") Set<IpAddressDownloadRate> specificIpDownloadRate,
                                        @JsonProperty("specificIpRestrictFileExtension") Set<IpAddressRestrictFileExtension> specificIpRestrictFileExtensions) {
        return new HttpConfig(port, welcomePage, maxConnection, waitConnection, rootDDirectoryPath, baseDownloadRate, baseRestrictFileExtension, specificIpDownloadRate,
                              specificIpRestrictFileExtensions);
    }

    private static HttpConfig create() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(path), HttpConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
