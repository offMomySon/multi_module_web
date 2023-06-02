package filter.pattern;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PatternUrls implements PatternUrl {
    private final List<PatternUrl> values;

    public PatternUrls(List<PatternUrl> values) {
        Objects.requireNonNull(values);
        List<PatternUrl> newValues = values.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());

        if (newValues.isEmpty()) {
            throw new RuntimeException("value is empty.");
        }

        this.values = newValues;
    }

    @Override
    public boolean isMatch(String requestUrl) {
        return values.stream().anyMatch(value -> value.isMatch(requestUrl));
    }
}
