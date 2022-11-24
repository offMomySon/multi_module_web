package filter.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import request.Uri;
import static validate.ValidateUtil.validateNull;

@Slf4j
@ToString
public class HttpConfig {
    private static final String PATH = "/config/http_config.json";
    public static final HttpConfig instance = create();

    private final int port;
    private final Uri welcomePage;

    private final Set<IpAddress> banIpAddresses;

    private final Uri resourceRootPath;

    private final Rate baseDownloadRate;

    private final Set<FileExtension> baseRestrictFileExtension;
    private final Set<IpAddressRate> specificDownloadRate;

    private final Set<IpAddressRestrictFileExtension> specificRestrictFileExtension;

    public Set<IpAddress> getBanIpAddresses() {
        return banIpAddresses;
    }

    private HttpConfig(int port, Uri welcomePage, int maxConnection, int waitConnection, int keepAliveTime, Set<IpAddress> banIpAddresses, Uri resourceRootPath, Rate baseDownloadRate,
                       Set<FileExtension> baseRestrictFileExtension, Set<IpAddressRate> specificDownloadRate, Set<IpAddressRestrictFileExtension> specificRestrictFileExtension) {
        if (port == 0 || port < 0) {
            throw new IllegalArgumentException("port is zero or minus");
        }
        if (maxConnection == 0 || maxConnection < 0) {
            throw new IllegalArgumentException("maxConnection is zero or minus");
        }
        if (waitConnection == 0 || waitConnection < 0) {
            throw new IllegalArgumentException("waitConnection is zero or minus");
        }
        if (keepAliveTime == 0 || keepAliveTime < 0) {
            throw new IllegalArgumentException("keepAliveTime is zero or minus");
        }

        validateNull(welcomePage);
        validateNull(resourceRootPath);
        validateNull(baseDownloadRate);
        validateNull(banIpAddresses);
        validateNull(baseRestrictFileExtension);
        validateNull(specificDownloadRate);
        validateNull(specificRestrictFileExtension);

        this.port = port;
        this.welcomePage = welcomePage;
        this.banIpAddresses = banIpAddresses;
        this.resourceRootPath = resourceRootPath;
        this.baseDownloadRate = baseDownloadRate;
        this.baseRestrictFileExtension = baseRestrictFileExtension;
        this.specificDownloadRate = specificDownloadRate;
        this.specificRestrictFileExtension = specificRestrictFileExtension;

        log.info("port : `{}`", port);
        log.info("welcomePage : `{}`", welcomePage);
        log.info("maxConnection : `{}`", maxConnection);
        log.info("waitConnection : `{}`", waitConnection);
        log.info("keepAliveTime : `{}`", keepAliveTime);
        log.info("resourceRootPath : `{}`", resourceRootPath);
        log.info("baseDownloadRate : `{}`", baseDownloadRate);
        log.info("baseRestrictFileExtension : `{}`", baseRestrictFileExtension);
        log.info("specificDownloadRate : `{}`", specificDownloadRate);
        log.info("specificRestrictFileExtension : `{}`", specificRestrictFileExtension);
    }

    @JsonCreator
    private static HttpConfig ofJackSon(@JsonProperty("port") int port,
                                        @JsonProperty("welcomePage") Uri welcomePage,
                                        @JsonProperty("maxConnection") int maxConnection,
                                        @JsonProperty("waitConnection") int waitConnection,
                                        @JsonProperty("keepAliveTime") int keepAliveTime,
                                        @JsonProperty("banIps") Set<IpAddress> banIpAddresses,
                                        @JsonProperty("resource") Uri rootDirectoryPath,
                                        @JsonProperty("downloadRate") Rate baseRate,
                                        @JsonProperty("restrictFileExtension") Set<FileExtension> baseRestrictFileExtension,
                                        @JsonProperty("specificIpDownloadRate") Set<IpAddressRate> specificIpDownloadRate,
                                        @JsonProperty("specificIpRestrictFileExtension") Set<IpAddressRestrictFileExtension> specificIpRestrictFileExtensions) {
        validateNull(welcomePage);
        validateNull(rootDirectoryPath);
        validateNull(baseRate);
        validateNull(banIpAddresses);
        validateNull(baseRestrictFileExtension);
        validateNull(specificIpDownloadRate);
        validateNull(specificIpRestrictFileExtensions);

        return new HttpConfig(port, welcomePage, maxConnection, waitConnection, keepAliveTime, banIpAddresses, rootDirectoryPath, baseRate, baseRestrictFileExtension, specificIpDownloadRate,
                              specificIpRestrictFileExtensions);
    }

    private static HttpConfig create() {
        InputStream resourceAsStream = HttpConfig.class.getResourceAsStream(PATH);
        URL resource = HttpConfig.class.getResource(PATH);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resourceAsStream, HttpConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public Uri getWelcomePage() {
        return welcomePage;
    }


    public Uri getResourceRootPath() {
        return resourceRootPath;
    }

    public Rate getBaseDownloadRate() {
        return baseDownloadRate;
    }

    public Set<FileExtension> getBaseRestrictFileExtension() {
        return baseRestrictFileExtension;
    }

    public Set<IpAddressRate> getSpecificDownloadRate() {
        return specificDownloadRate;
    }

    public Set<IpAddressRestrictFileExtension> getSpecificRestrictFileExtension() {
        return specificRestrictFileExtension;
    }
}
