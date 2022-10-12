package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import lombok.ToString;

@ToString
public class DownloadRate {
    private final Duration duration;
    private final int count;

    private DownloadRate(Duration duration, int count) {
        this.duration = duration;
        this.count = count;
    }

    @JsonCreator
    private static DownloadRate ofJackSon(@JsonProperty("period") long period, @JsonProperty("count") int count) {
        Duration duration = Duration.ofMillis(period);

        return new DownloadRate(duration, count);
    }
}
