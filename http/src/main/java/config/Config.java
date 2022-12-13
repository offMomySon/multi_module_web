package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import static validate.ValidateUtil.*;

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
        this.maxConnection = validateNull(maxConnection);
        this.waitConnection = validateNull(waitConnection);
        this.keepAliveTime = validateNull(keepAliveTime);
        this.port = validateNull(port);

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
        validateNull(maxConnection);
        validateNull(waitConnection);
        validateNull(keepAliveTime);
        validateNull(port);

        return new Config(maxConnection, waitConnection, keepAliveTime, port);
    }

    private static Config create() {
        InputStream resourceInputStream = Config.class.getResourceAsStream(PATH);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resourceInputStream, Config.class);
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to create config class. reason `{0}`", e.getCause()), e);
        }
    }
}
