package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static validate.ValidateUtil.validateNull;

@Slf4j
@ToString
public class IpAddressRate {
    private final IpAddress ipAddress;
    private final Rate rate;

    private IpAddressRate(IpAddress ipAddress, Rate rate) {
        validateNull(ipAddress);
        validateNull(rate);

        this.ipAddress = ipAddress;
        this.rate = rate;
    }

    @JsonCreator
    private static IpAddressRate ofJackson(@JsonProperty("ip") IpAddress ipAddress,
                                           @JsonProperty("downloadRate") Rate rate) {
        validateNull(ipAddress);
        validateNull(rate);

        return new IpAddressRate(ipAddress, rate);
    }
}
