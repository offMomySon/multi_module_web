package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Set;
import lombok.ToString;

@ToString
public class DownloadSetting {
    private final String ipAddress;
    private final DownloadsRate downloadsRate;
    private final Set<String> restrictFileExtensions;

    private DownloadSetting(String ipAddress, DownloadsRate downloadsRate, Set<String> restrictFileExtensions) {
        this.ipAddress = ipAddress;
        this.downloadsRate = downloadsRate;
        this.restrictFileExtensions = restrictFileExtensions;

    }

    @JsonCreator
    private static DownloadSetting ofJackSon(@JsonProperty("ip") String ipAddress,
                                             @JsonProperty("downloadRate") DownloadsRate downloadsRate,
                                             @JsonProperty("restrictedFileExtension") Set<String> restrictFileExtensions){

        return new DownloadSetting(ipAddress, downloadsRate, restrictFileExtensions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadSetting that = (DownloadSetting) o;
        return Objects.equals(ipAddress, that.ipAddress) && Objects.equals(downloadsRate, that.downloadsRate) && Objects.equals(restrictFileExtensions,
                                                                                                                                that.restrictFileExtensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress, downloadsRate, restrictFileExtensions);
    }
}
