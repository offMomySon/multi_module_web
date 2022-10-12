package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public class AddressRestrictFileExtension {
    private final String address;
    private final Set<String> restrictFileExtension;

    private AddressRestrictFileExtension(String address, Set<String> restrictFileExtension) {
        this.address = address;
        this.restrictFileExtension = restrictFileExtension;
    }

    @JsonCreator
    private static AddressRestrictFileExtension ofJackSon(@JsonProperty("ip") String address,
                                                          @JsonProperty("restrictFileExtension") Set<String> restrictFileExtensions) {

        return new AddressRestrictFileExtension(address, restrictFileExtensions);
    }

}
