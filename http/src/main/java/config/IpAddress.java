package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.NonNull;
import lombok.ToString;
import org.apache.http.conn.util.InetAddressUtils;
import static validate.ValidateUtil.validate;
import static validate.ValidateUtil.validateNull;

@ToString
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

    public static IpAddress from(InetSocketAddress socketAddress){
        validateNull(socketAddress);

        String hostAddress = socketAddress.getAddress().getHostAddress();

        return new IpAddress(hostAddress);
    }

    @JsonCreator
    private static IpAddress ofJackSon(@NonNull String value) {
        validate(value);

        return new IpAddress(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpAddress ipAddress = (IpAddress) o;
        return Objects.equals(value, ipAddress.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
