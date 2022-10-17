package request;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.ToString;
import static util.ValidateUtil.validate;
import static util.ValidateUtil.validateNull;

@ToString
public class Uri {
    private static String[] NOT_ALLOWED_SPECIAL_CHARACTER = new String[]{"\"", "!", "@", "#", "$", "%", "&", "*", "(", ")", "'", "+", ",", "-", ".", ":", ";", "<", "=", ">", "?", "[", "]", "^", "_", "`", "{", "|", "}", "\\"};
    private static String QUERY_DELIMITER = "\\?";

    private final Path value;

    private Uri(Path path) {
        validateNull(path);

        path = path.normalize();

        String[] splitUri = path.toString().split(QUERY_DELIMITER, 2);
        String uri = splitUri[0];

        validateSpecialCharacter(uri);

        this.value = path;
    }

    private static void validateSpecialCharacter(String uri) {
        for (int index = 0; index < NOT_ALLOWED_SPECIAL_CHARACTER.length; index++) {
            String specialCharacter = NOT_ALLOWED_SPECIAL_CHARACTER[index];

            boolean hasSpecialCharacter = uri.contains(specialCharacter);
            if(hasSpecialCharacter){
                throw new RuntimeException(MessageFormat.format("It must not contain special characters. contained : {0}", specialCharacter));
            }
        }
    }

    public static Uri of(String _path) {
        validate(_path);

        Path path = Paths.get(_path);
        return new Uri(path);
    }

    @JsonCreator
    private static Uri ofJackSon(String path) {
        validate(path);
        return of(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Uri uri = (Uri) o;
        return Objects.equals(value, uri.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
