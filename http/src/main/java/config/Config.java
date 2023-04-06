package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Config {
    private static final String PATH = "/config.json";
    public static final Config INSTANCE = create();

    private final Integer maxConnection;
    private final Integer waitConnection;
    private final Long keepAliveTime;
    private final Integer port;

    public Integer getMaxConnection() {
        return maxConnection;
    }

    public Integer getWaitConnection() {
        return waitConnection;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public Integer getPort() {
        return port;
    }

    private Config(Integer maxConnection, Integer waitConnection, Long keepAliveTime, Integer port) {
        Objects.requireNonNull(maxConnection);
        Objects.requireNonNull(waitConnection);
        Objects.requireNonNull(keepAliveTime);
        Objects.requireNonNull(port);

        this.maxConnection = maxConnection;
        this.waitConnection = waitConnection;
        this.keepAliveTime = keepAliveTime;
        this.port = port;

        log.info("maxConnection : `{}`", this.maxConnection);
        log.info("waitConnection : `{}`", this.waitConnection);
        log.info("keepAliveTime : `{}`", this.keepAliveTime);
        log.info("port : `{}`", this.port);
    }

    @JsonCreator
    private static Config ofJackSon(@JsonProperty("maxConnection") Integer maxConnection,
                                    @JsonProperty("waitConnection") Integer waitConnection,
                                    @JsonProperty("keepAliveTime") Long keepAliveTime,
                                    @JsonProperty("port") Integer port) {
        Objects.requireNonNull(maxConnection);
        Objects.requireNonNull(waitConnection);
        Objects.requireNonNull(keepAliveTime);
        Objects.requireNonNull(port);

        return new Config(maxConnection, waitConnection, keepAliveTime, port);
    }

    private static Config create() {
        InputStream resourceInputStream = Config.class.getResourceAsStream(PATH);
        try {
            JsonMapper jsonMapper = new JsonMapper();
            return jsonMapper.readValue(resourceInputStream, Config.class);
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to create config class. reason `{0}`", e.getCause()), e);
        }
    }
}
