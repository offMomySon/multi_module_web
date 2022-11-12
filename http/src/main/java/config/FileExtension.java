package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Objects;
import lombok.ToString;
import static validate.ValidateUtil.validate;

@ToString
public class FileExtension {
    private static final String EXTENSION_STARTER = ".";

    private final String value;

    private FileExtension(String value) {
        validate(value);

        boolean doesNotFileExtensionFormat = value.indexOf(EXTENSION_STARTER) != 0;
        if (doesNotFileExtensionFormat) {
            throw new RuntimeException("value does not file extension format.");
        }

        this.value = value;
    }

    @JsonCreator
    private static FileExtension ofJackSon(String fileExtension) {
        validate(fileExtension);

        return new FileExtension(fileExtension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileExtension that = (FileExtension) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
