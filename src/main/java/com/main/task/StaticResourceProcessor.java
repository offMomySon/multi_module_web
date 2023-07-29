package com.main.task;

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

    // 시나리오분석.
//    1. request, response 를 받는다.
//    2. request 로부터 request Path 를 가져온다.
//    3. request path 를 이용하여 resource 의 path 를 찾아온다.
//      1. resource 를 찾기 위한 path 를 받는다.
//      2. findUrl 을 일반화한다.
//      3. 등록된 url 에 findUrl 이 존재하는 지 확인한다.
//      4. resource directory path, find url path 를 조합하여 resource path 를 생성한다.
//    4. resource 의 확장자를 추출한다.
//    5. resource 확장자를 이용하여 response header 값을 셋팅한다.
//    6. resource 를 전송하기 위해 inputStream 으로 변환한다.
//    7. httpResponse 로 부터 httpResponseWriter 를 가져온다.
//    8. response header, resource inputStream 을 전송한다.

//    1. request path 로 부터 resource 를 가져온다.
//    2. resource extension 에 따라 http resposne header 값을 셋팅한다.
//    3. http header, body 를 받은 http response 을 전송한다.
    public boolean execute(HttpRequest request, HttpResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        Path requestUrl = request.getHttpRequestPath().getValue();
        log.info("requestUrl : {}", requestUrl);

        Optional<Path> optionalResourcePath = packageResourceFinder.findResource(requestUrl);
        if (optionalResourcePath.isEmpty()) {
            log.info("does not exist resource.");
            return false;
        }

        Path resource = optionalResourcePath.get();
        log.info("resource : {}", resource);

        String fileExtension = getFileExtension(resource);
        response = setHttpResponseHeader(response, fileExtension);

        InputStream fileInputStream = getResourceInputStream(resource);
        HttpResponseWriter sender = response.getSender();
        sender.send(fileInputStream);
        return true;
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

    private static InputStream getResourceInputStream(Path resource) {
        try {
            return new BufferedInputStream(new FileInputStream(resource.toString()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
