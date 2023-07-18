package com.main.executor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.PackageResourceFinder;
import processor.HttpRequestProcessor;
import vo.HttpRequest;
import vo.HttpResponse;
import vo.HttpResponseWriter;

@Slf4j
public class StaticResourceProcessor implements HttpRequestProcessor {
    private final PackageResourceFinder packageResourceFinder;

    public StaticResourceProcessor(PackageResourceFinder packageResourceFinder) {
        Objects.requireNonNull(packageResourceFinder);
        this.packageResourceFinder = packageResourceFinder;
    }

//    기능 분석 및 기능 리스트 정리.
//    http request, response 를 받는다.
//    http request url 을 가져온다.
//    http request url 을 path url 로 변환한다.

//    packageResourceFinder 을 이용하여 path url 에 해당하는 resource 의 path 를 가져온다.

//    resource 의 file 이름을 가져온다.
//    file 이름에서 확장자를 가져온다.
//    확장자에 따라 http response header 를 설정한다.

//    httpResponse 로 부터 httpResponseWriter 를 가져온다.
//    resource path 를 inputStream 으로 변환한다.
//    inputStream 을 httpResponseWriter 를 전송한다.

    //    키워드 추출.
//
    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        String requestUrl = request.getHttpUri().getUrl();
        Path newRequestUrl = Path.of(requestUrl);
        log.info("newRequestUrl : {}", newRequestUrl);

        Optional<Path> optionalResource = packageResourceFinder.find(newRequestUrl);
        if (optionalResource.isEmpty()) {
            log.info("does not exist resource.");
            return false;
        }

        Path resource = optionalResource.get();
        log.info("resource : {}", resource);
        String fileExtension = getFileExtension(resource);
        response = setHttpResponseHeader(response, fileExtension);

        InputStream fileInputStream = getResourceInputStream(resource);
        HttpResponseWriter sender = response.getSender();
        sender.send(fileInputStream);
        return true;
    }

    private static InputStream getResourceInputStream(Path resource) {
        try {
            return new BufferedInputStream(new FileInputStream(resource.toString()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpResponse setHttpResponseHeader(HttpResponse response, String fileExtension) {
        log.info("fileExtension : {}", fileExtension);
        switch (fileExtension) {
            case "jpg":
                log.info("jpg : {}", fileExtension);
                response.setStartLine("HTTP/1.1 200 OK");
                response.appendHeader(Map.of(
                    "Date", "MON, 27 Jul 2023 12:28:53 GMT",
                    "Host", "localhost:8080",
                    "Connection", "close",
                    "Content-Type", "image/jpeg"));
                return response;
            case "txt":
                log.info("txt : {}", fileExtension);
                response.setStartLine("HTTP/1.1 200 OK");
                response.appendHeader(Map.of(
                    "Date", "MON, 27 Jul 2023 12:28:53 GMT",
                    "Host", "localhost:8080",
                    "Connection", "close",
                    "Content-Type", "text/html; charset=UTF-8"));
                return response;
        }
        throw new RuntimeException("does exist match fileExtension");
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
