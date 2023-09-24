package filter;

import java.util.Objects;
import lombok.Getter;


@Getter
public class FilterInfo {
    private final String name;
    private final String pattern;
    private final FilterWorker filterWorker;

    public FilterInfo(String name, String pattern, FilterWorker filterWorker) {
        if(Objects.isNull(name) || name.isBlank()){
            throw new RuntimeException("name is empty.");
        }
        if(Objects.isNull(pattern) || pattern.isBlank()){
            throw new RuntimeException("pattern is empty.");
        }
        if(Objects.isNull(filterWorker)){
            throw new RuntimeException("filterWorker is empty.");
        }
        this.name = name;
        this.pattern = pattern;
        this.filterWorker = filterWorker;
    }
}