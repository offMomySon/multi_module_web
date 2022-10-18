package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.ToString;
import static util.ValidateUtil.validateNull;

@ToString
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
}
