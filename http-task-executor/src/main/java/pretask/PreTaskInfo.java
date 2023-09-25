package pretask;

import java.util.Objects;
import lombok.Getter;


@Getter
public class PreTaskInfo {
    private final String name;
    private final String pattern;
    private final PreTaskWorker preTaskWorker;

    public PreTaskInfo(String name, String pattern, PreTaskWorker preTaskWorker) {
        if(Objects.isNull(name) || name.isBlank()){
            throw new RuntimeException("name is empty.");
        }
        if(Objects.isNull(pattern) || pattern.isBlank()){
            throw new RuntimeException("pattern is empty.");
        }
        if(Objects.isNull(preTaskWorker)){
            throw new RuntimeException("filterWorker is empty.");
        }
        this.name = name;
        this.pattern = pattern;
        this.preTaskWorker = preTaskWorker;
    }
}