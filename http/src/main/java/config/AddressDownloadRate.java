package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class AddressDownloadRate {
    private final String address;
    private final DownloadRate downloadRate;

    private AddressDownloadRate(String address, DownloadRate downloadRate) {
        this.address = address;
        this.downloadRate = downloadRate;
    }

    @JsonCreator
    private static AddressDownloadRate ofJackson(@JsonProperty("ip") String address,
                                                 @JsonProperty("downloadRate") DownloadRate downloadRate){

        return new AddressDownloadRate(address, downloadRate);
    }
}
