package executor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import processor.HttpRequestExecutor;
import read.FileFinder;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;

@Slf4j
public class HttpStaticResourceExecutor implements HttpRequestExecutor {
    private final FileFinder fileFinder;

    public HttpStaticResourceExecutor(FileFinder fileFinder) {
        Objects.requireNonNull(fileFinder);
        this.fileFinder = fileFinder;
    }

    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        String url = request.getHttpUri().getUrl();
        log.info("url : {}", url);
        Path requestUrl = Path.of(url);
        log.info("requestUrl : {}", requestUrl);

        Optional<Path> optionalResource = fileFinder.findResource(requestUrl);

        if (optionalResource.isEmpty()) {
            return false;
        }

        Path file = optionalResource.get();
        log.info("file : {}", file);

        setHttpResponse(response, file);

        HttpResponseWriter sender = response.getSender();

        try {
            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file.toString()));
            sender.send(fileInputStream);

            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setHttpResponse(HttpResponse response, Path file) {
        String fileExtension = getFileExtension(file);
        log.info("fileExtension : {}", fileExtension);

        switch (fileExtension) {
            case "jpg":
                log.info("jpg : {}", fileExtension);
                setJpgHttpResponse(response);
                break;
            case "txt":
                log.info("txt : {}", fileExtension);
                setTextHttpResponse(response);
                break;
        }
    }

    private static void setJpgHttpResponse(HttpResponse response) {
        response.setStartLine("HTTP/1.1 200 OK");
        response.appendHeader(Map.of(
            "Date", "MON, 27 Jul 2023 12:28:53 GMT",
            "Host", "localhost:8080",
            "Connection", "close",
            "Content-Type", "image/jpeg"));
    }

    private static void setTextHttpResponse(HttpResponse response) {
        response.setStartLine("HTTP/1.1 200 OK");
        response.appendHeader(Map.of(
            "Date", "MON, 27 Jul 2023 12:28:53 GMT",
            "Host", "localhost:8080",
            "Connection", "close",
            "Content-Type", "text/html; charset=UTF-8"));
    }


    private static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        log.info("fileName : {}", fileName);

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1);
    }
}
