package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import request.FilePath;
import static util.ValidateUtil.validateNull;

@Slf4j
@ToString
public class HttpConfig {
    private static final String path = "http/src/main/resources/config/http_config.json";
    public static final HttpConfig instance = create();

    private final int port;
    private final FilePath welcomePage;

    private final int maxConnection;
    private final int waitConnection;

    private final FilePath resourceRootPath;

    private final Rate baseDownloadRate;
    private final Set<FileExtension> baseRestrictFileExtension;

    private final Set<IpAddressRate> specificDownloadRate;
    private final Set<IpAddressRestrictFileExtension> specificRestrictFileExtension;

    public HttpConfig(int port, FilePath welcomePage, int maxConnection, int waitConnection, FilePath resourceRootPath, Rate baseDownloadRate,
                      Set<FileExtension> baseRestrictFileExtension, Set<IpAddressRate> specificDownloadRate, Set<IpAddressRestrictFileExtension> specificRestrictFileExtension) {
        if (port == 0 || port < 0) {
            throw new IllegalArgumentException("port is zero or minus");
        }
        if (maxConnection == 0 || maxConnection < 0){
            throw new IllegalArgumentException("maxConnection is zero or minus");
        }
        if(waitConnection == 0 || waitConnection <0){
            throw new IllegalArgumentException("waitConnection is zero or minux");
        }

        validateNull(welcomePage);
        validateNull(resourceRootPath);
        validateNull(baseDownloadRate);
        validateNull(baseRestrictFileExtension);
        validateNull(specificDownloadRate);
        validateNull(specificRestrictFileExtension);

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
                                        @JsonProperty("welcomePage") FilePath welcomePage,
                                        @JsonProperty("maxConnection") int maxConnection,
                                        @JsonProperty("waitConnection") int waitConnection,
                                        @JsonProperty("resource") FilePath rootDirectoryPath,
                                        @JsonProperty("downloadRate") Rate baseRate,
                                        @JsonProperty("restrictFileExtension") Set<FileExtension> baseRestrictFileExtension,
                                        @JsonProperty("specificIpDownloadRate") Set<IpAddressRate> specificIpDownloadRate,
                                        @JsonProperty("specificIpRestrictFileExtension") Set<IpAddressRestrictFileExtension> specificIpRestrictFileExtensions) {
        validateNull(welcomePage);
        validateNull(rootDirectoryPath);
        validateNull(baseRate);
        validateNull(baseRestrictFileExtension);
        validateNull(specificIpDownloadRate);
        validateNull(specificIpRestrictFileExtensions);

        return new HttpConfig(port, welcomePage, maxConnection, waitConnection, rootDirectoryPath, baseRate, baseRestrictFileExtension, specificIpDownloadRate,
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
