package task;

import com.main.util.IoUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.Optional;
import parameter.matcher.ParameterAndValueMatcherType;
import task.endpoint.EndPointTask;
import vo.ContentType;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTextEndPointTask implements HttpEndPointTask{
    private static final ContentType TEXT_HTML = ContentType.TEXT_HTML;

    private final EndPointTask endPointTask;

    public HttpTextEndPointTask(EndPointTask endPointTask) {
        Objects.requireNonNull(endPointTask);
        this.endPointTask = endPointTask;
    }

    @Override
    public ParameterAndValueMatcherType[] getParameterTypeInfos() {
        return endPointTask.getParameterTypeInfos();
    }

    @Override
    public Optional<HttpTaskResult> execute(Object[] params) {
        Optional<Object> optionalExecuteResult = endPointTask.execute(params);
        if(optionalExecuteResult.isEmpty()){
            throw new RuntimeException("Does not exist execute Result.");
        }

        String result = (String) optionalExecuteResult.get();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(result.getBytes(UTF_8));
        BufferedInputStream bufferedInputStream = IoUtils.createBufferedInputStream(byteArrayInputStream);
        HttpTaskResult httpTaskResult = new HttpTaskResult(TEXT_HTML, bufferedInputStream);
        return Optional.of(httpTaskResult);
    }
}
