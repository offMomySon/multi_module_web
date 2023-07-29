package com.main.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceHttpResponseHeaderValueCreator {
    private final Path resource;

    public ResourceHttpResponseHeaderValueCreator(Path resource) {
        Objects.requireNonNull(resource);
        this.resource = resource;
    }

    public HttpHeaderValue create(Optional<Path> optionalResource) {
        String date = getDateTime();
        String host = getLocalDomain();
        String connection = "close";


        if (optionalResource.isEmpty()) {
            String contentType = "Content-Type: text/plain; charset=utf-8";
            Map<String, String> header = Map.of("date", getDateTime(),
                                                "host", getLocalDomain(),
                                                "connection", "close",
                                                "contentType", contentType);
            return new HttpHeaderValue("HTTP/1.1 404 Not Found", header);
        }

        Path resource = optionalResource.get();
        String fileExtension = getFileExtension(resource);
        String contentType = getContentType(fileExtension);
        Map<String, String> header = Map.of("date", getDateTime(),
                                            "host", getLocalDomain(),
                                            "connection", "close",
                                            "contentType", contentType);
        return new HttpHeaderValue("HTTP/1.1 200 OK", header);
    }

    private static String getLocalDomain() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String localDomain = localHost.getCanonicalHostName();

            // Extract the domain from the full hostname if needed
            int indexOfDomain = localDomain.indexOf('.');
            if (indexOfDomain != -1) {
                localDomain = localDomain.substring(indexOfDomain + 1);
            }

            return localDomain;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDateTime() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        return now.format(formatter);
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

    private static String getContentType(String fileExtension) {
        log.info("fileExtension : {}", fileExtension);
        switch (fileExtension) {
            case "jpg":
                return "image/jpeg";
            case "txt":
                return "text/html; charset=UTF-8";
        }
        throw new RuntimeException("does exist match fileExtension");
    }

    public static class HttpHeaderValue {
        private final String startLine;
        private final Map<String, String> headerValue;

        public HttpHeaderValue(String startLine, Map<String, String> headerValue) {
            Objects.requireNonNull(startLine);
            Objects.requireNonNull(headerValue);
            headerValue = headerValue.entrySet().stream()
                .filter(entry -> Objects.isNull(entry.getKey()))
                .filter(entry -> Objects.isNull(entry.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

            if (headerValue.isEmpty()) {
                throw new RuntimeException("headerValue is empty.");
            }

            this.startLine = startLine;
            this.headerValue = headerValue;
        }

        public String getStartLine() {
            return startLine;
        }

        public Map<String, String> getHeaderValue() {
            return headerValue;
        }
    }
}
