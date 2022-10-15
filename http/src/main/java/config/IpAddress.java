package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import lombok.NonNull;
import lombok.ToString;
import org.apache.http.conn.util.InetAddressUtils;
import static util.ValidateUtil.validate;
import static util.ValidateUtil.validateNull;

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
}
