package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.text.MessageFormat;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.util.InetAddressUtils;

public class IpAddress {
    private final String value;

    private IpAddress(String value) {
        validate(value);

        boolean doesNotIpv4Address = !InetAddressUtils.isIPv4Address(value);
        if (doesNotIpv4Address) {
            throw new RuntimeException(MessageFormat.format("Not ipv4 address : `{}`", value));
        }

        this.value = value;
    }

    @JsonCreator
    private static IpAddress ofJackSon(@NonNull String value) {
        validate(value);

        return new IpAddress(value);
    }

    private static void validate(String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isBlank(value)) {
            throw new RuntimeException(MessageFormat.format("value is invalid : `{}`", value));
        }
    }
}
