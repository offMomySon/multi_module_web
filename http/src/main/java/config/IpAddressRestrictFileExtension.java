package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;

public class IpAddressRestrictFileExtension {
    private final IpAddress ipAddress;
    private final Set<FileExtension> restrictFileExtension;

    private IpAddressRestrictFileExtension(IpAddress ipAddress, Set<FileExtension> restrictFileExtension) {
        validateNull(ipAddress);
        validateNull(restrictFileExtension);
        if (restrictFileExtension.size() == 0) {
            throw new RuntimeException("restrictFileExtension is zero size");
        }

        this.ipAddress = ipAddress;
        this.restrictFileExtension = restrictFileExtension;
    }

    @JsonCreator
    private static IpAddressRestrictFileExtension ofJackSon(@JsonProperty("ip") IpAddress ipAddress,
                                                            @JsonProperty("restrictFileExtension") Set<FileExtension> restrictFileExtensions) {
        validateNull(ipAddress);
        validateNull(restrictFileExtensions);
        return new IpAddressRestrictFileExtension(ipAddress, restrictFileExtensions);
    }

    private static <T> void validateNull(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. clazz : `{}`", value.getClass()));
        }
    }

}
