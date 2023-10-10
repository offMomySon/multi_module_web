package pretask;

import java.util.Objects;
import lombok.Getter;
import task.PostTaskWorker;
import task.PreTaskWorker;


@Getter
public class PostTaskInfo {
    private final String name;
    private final String pattern;
    private final PostTaskWorker postTaskWorker;

    public PostTaskInfo(String name, String pattern, PostTaskWorker postTaskWorker) {
        if(Objects.isNull(name) || name.isBlank()){
            throw new RuntimeException("name is empty.");
        }
        if(Objects.isNull(pattern) || pattern.isBlank()){
            throw new RuntimeException("pattern is empty.");
        }
        if(Objects.isNull(postTaskWorker)){
            throw new RuntimeException("filterWorker is empty.");
        }
        this.name = name;
        this.pattern = pattern;
        this.postTaskWorker = postTaskWorker;
    }
}