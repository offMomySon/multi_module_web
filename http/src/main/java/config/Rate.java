package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Objects;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class Rate {
    private final Duration duration;
    private final int count;

    private Rate(Duration duration, int count) {
        if (Objects.isNull(duration) || duration.isNegative() || duration.isZero()) {
            throw new RuntimeException(MessageFormat.format("Duration is invalid value : `{}", duration));
        }
        if (count < 0 || count == 0) {
            throw new RuntimeException(MessageFormat.format("count is minus or zero value : `{}`", count));
        }

        this.duration = duration;
        this.count = count;
    }

    @JsonCreator
    private static Rate ofJackSon(@JsonProperty("period") long period,
                                  @JsonProperty("count") int count) {
        Duration duration = Duration.ofMillis(period);

        return new Rate(duration, count);
    }
}
