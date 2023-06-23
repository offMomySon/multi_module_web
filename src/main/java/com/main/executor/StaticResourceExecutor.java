package com.main.executor;

import com.main.container.resource.ResourceFinder;
import com.main.container.resource.ResourceUrls;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import processor.HttpRequestExecutor;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;

@Slf4j
public class StaticResourceExecutor implements HttpRequestExecutor {
    private final ResourceUrls resourceUrls;
    private final ResourceFinder resourceFinder;

    public StaticResourceExecutor(ResourceFinder resourceFinder) {
        Objects.requireNonNull(resourceFinder);
        this.resourceUrls = resourceFinder.extractResourceUrls();
        this.resourceFinder = resourceFinder;
    }

    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        String requestUrl = request.getHttpUri().getUrl();
        Path newRequestUrl = Path.of(requestUrl);
        log.info("newRequestUrl : {}", newRequestUrl);

        boolean doesNotExistMatchUrl = !resourceUrls.contain(newRequestUrl);
        if (doesNotExistMatchUrl) {
            log.info("does not exist MatchUrl.");
            return false;
        }

        Optional<Path> optionalResource = resourceFinder.findResource(newRequestUrl);
        if (optionalResource.isEmpty()) {
            log.info("does not exist resource.");
            return false;
        }

        Path resource = optionalResource.get();
        log.info("resource : {}", resource);

        setHttpResponse(response, resource);
        sendHttpResponse(response, resource);
        return true;
    }

    private static void sendHttpResponse(HttpResponse response, Path file) {
        HttpResponseWriter sender = response.getSender();
        try {
            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file.toString()));
            sender.send(fileInputStream);
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
