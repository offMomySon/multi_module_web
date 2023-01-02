package mapper;

import java.lang.reflect.Method;
import lombok.ToString;

@ToString
public class TaskActuator {
    private final TaskIndicator taskIndicator;
    private final Method method;


    public TaskActuator(TaskIndicator taskIndicator, Method method) {
        this.taskIndicator = taskIndicator;
        this.method = method;
    }

}
