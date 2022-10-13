package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class IpAddressDownloadRate {
    private final IpAddress ipAddress;
    private final DownloadRate downloadRate;

    private IpAddressDownloadRate(IpAddress ipAddress, DownloadRate downloadRate) {
        validateNull(ipAddress);
        validateNull(downloadRate);

        this.ipAddress = ipAddress;
        this.downloadRate = downloadRate;
    }

    @JsonCreator
    private static IpAddressDownloadRate ofJackson(@JsonProperty("ip") IpAddress ipAddress,
                                                   @JsonProperty("downloadRate") DownloadRate downloadRate) {
        validateNull(ipAddress);
        validateNull(downloadRate);

        return new IpAddressDownloadRate(ipAddress, downloadRate);
    }

    private static <T> void validateNull(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. clazz : `{}`", value.getClass()));
        }
    }
}
