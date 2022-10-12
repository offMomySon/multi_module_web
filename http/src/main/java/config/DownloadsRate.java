package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import lombok.ToString;

@ToString
public class DownloadsRate {
    private final Duration duration;
    private final int count;

    private DownloadsRate(Duration duration, int count) {
        this.duration = duration;
        this.count = count;
    }

    @JsonCreator
    private static DownloadsRate ofJackSon(@JsonProperty("period") long period, @JsonProperty("count") int count) {
        Duration duration = Duration.ofMillis(period);

        return new DownloadsRate(duration, count);
    }
}
