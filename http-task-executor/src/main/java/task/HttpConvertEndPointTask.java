package task;

import converter.Converter;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import task.endpoint.EndPointTask;
import vo.ContentType;

public class HttpConvertEndPointTask implements HttpEndPointTask{
    private final ContentType contentType;
    private final Converter converter;
    private final EndPointTask endPointTask;

    public HttpConvertEndPointTask(ContentType contentType, Converter converter, EndPointTask endPointTask) {
        Objects.requireNonNull(contentType);
        Objects.requireNonNull(converter);
        Objects.requireNonNull(endPointTask);
        this.contentType = contentType;
        this.converter = converter;
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
        InputStream inputStream = converter.convertToInputStream(o);
        HttpTaskResult httpTaskResult = new HttpTaskResult(contentType, inputStream);
        return Optional.of(httpTaskResult);
    }
}
