package task;

import converter.ValueConverter;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import parameter.matcher.ParameterAndValueMatcherType;
import task.worker.EndPointTaskWorker;
import vo.ContentType;

public class HttpConvertEndPointTask implements HttpEndPointTask{
    private final ContentType contentType;
    private final ValueConverter valueConverter;
    private final EndPointTaskWorker endPointTaskWorker;

    public HttpConvertEndPointTask(ContentType contentType, ValueConverter valueConverter, EndPointTaskWorker endPointTaskWorker) {
        Objects.requireNonNull(contentType);
        Objects.requireNonNull(valueConverter);
        Objects.requireNonNull(endPointTaskWorker);
        this.contentType = contentType;
        this.valueConverter = valueConverter;
        this.endPointTaskWorker = endPointTaskWorker;
    }

    @Override
    public ParameterAndValueMatcherType[] getParameterTypeInfos() {
        return this.endPointTaskWorker.getParameterTypeInfos();
    }

    @Override
    public Optional<HttpTaskResult> execute(Object[] params) {
        Objects.requireNonNull(params);

        Optional<Object> optionalExecuteResult =  endPointTaskWorker.execute(params);
        if(optionalExecuteResult.isEmpty()){
            throw new RuntimeException("Does not exist execute Result.");
        }

        Object o = optionalExecuteResult.get();
        InputStream inputStream = valueConverter.convertToInputStream(o);
        HttpTaskResult httpTaskResult = new HttpTaskResult(contentType, inputStream);
        return Optional.of(httpTaskResult);
    }
}
