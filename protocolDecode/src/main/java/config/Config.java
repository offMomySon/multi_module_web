package config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import static validate.ValidateUtil.validateNull;

@Getter
public class Config {
//    private final Config INSTANCE = new
    private static final String PATH = "resources/config.json";

    private final Long port;
    private final Long corePoolSize;
    private final Long maximumPoolSize;
    private final Long keepAliveTime;

    private Config(Long port, Long corePoolSize, Long maximumPoolSize, Long keepAliveTime) {
        this.port = validateNull(port);
        this.corePoolSize = validateNull(corePoolSize);
        this.maximumPoolSize = validateNull(maximumPoolSize);
        this.keepAliveTime = validateNull(keepAliveTime);
    }

    @JsonCreator
    private static Config ofJackSon(@JsonProperty("port") Long port,
                                    @JsonProperty("corePoolSize") Long corePoolSize,
                                    @JsonProperty("maximumPoolSize") Long maximumPoolSize,
                                    @JsonProperty("keepAliveTime") Long keepAliveTime) {
        validateNull(port);
        validateNull(corePoolSize);
        validateNull(maximumPoolSize);
        validateNull(keepAliveTime);

        return new Config(port, corePoolSize, maximumPoolSize, keepAliveTime);
    }


    private static Config create() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(new File(PATH), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
