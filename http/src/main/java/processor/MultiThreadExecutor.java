package processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import request.Uri;
import response.Responser;
import util.ContentFinder;
import static util.ValidateUtil.*;

@Slf4j
public class MultiThreadExecutor implements Executor {
    private static final ContentFinder contentFinder = new ContentFinder();
    private final ThreadPoolExecutor threadPoolExecutor;
    private final OutputStream outputStream;
    private final Uri uri;

    public MultiThreadExecutor(ThreadPoolExecutor threadPoolExecutor, OutputStream outputStream, Uri uri) {
        validateNull(threadPoolExecutor);
        validateNull(outputStream);
        validateNull(uri);

        this.threadPoolExecutor = threadPoolExecutor;
        this.outputStream = outputStream;
        this.uri = uri;
    }

    public void execute() {
        threadPoolExecutor.execute(() -> {
            try {
                BufferedInputStream contentInputStream = contentFinder.createInputStream(uri.getValue());

                Responser responser = Responser.build()
                    .contentType(Responser.ContentType.TEXT)
                    .status(Responser.Status.OK)
                    .socketOutputStream(outputStream)
                    .contentInputStream(contentInputStream)
                    .build();

                responser.send();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
