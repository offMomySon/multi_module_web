package task;

import converter.ValueConverter;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import task.endpoint.EndPointTask;
import vo.ContentType;

public class HttpConvertEndPointTask implements HttpEndPointTask{
    private final ContentType contentType;
    private final ValueConverter valueConverter;
    private final EndPointTask endPointTask;

    public HttpConvertEndPointTask(ContentType contentType, ValueConverter valueConverter, EndPointTask endPointTask) {
        Objects.requireNonNull(contentType);
        Objects.requireNonNull(valueConverter);
        Objects.requireNonNull(endPointTask);
        this.contentType = contentType;
        this.valueConverter = valueConverter;
        this.endPointTask = endPointTask;
    }

    @Override
    public Parameter[] getExecuteParameters() {
        return this.endPointTask.getExecuteParameters();
    }

    @Override
    public Optional<HttpTaskResult> execute(Object[] params) {
        Objects.requireNonNull(params);

        Optional<Object> optionalExecuteResult =  endPointTask.execute(params);
        if(optionalExecuteResult.isEmpty()){
            throw new RuntimeException("Does not exist execute Result.");
        }

        Object o = optionalExecuteResult.get();
        InputStream inputStream = valueConverter.convertToInputStream(o);
        HttpTaskResult httpTaskResult = new HttpTaskResult(contentType, inputStream);
        return Optional.of(httpTaskResult);
    }
}
