package com.main.task.response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceHttpResponseCreator implements HttpResponseCreator {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String HOST_ADDRESS;

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HOST_ADDRESS = getHostAddress();
    }

    private final Path path;

    public ResourceHttpResponseCreator(Path path) {
        Objects.requireNonNull(path);
        this.path = path;
    }

    @Override
    public HttpResponse create() {
        String fileExtension = getFileExtension(path);

        String startLine = "HTTP/1.1 200 OK";
        Map<String, String> header = Map.of(
            "Date", SIMPLE_DATE_FORMAT.format(new Date()),
            "Host", HOST_ADDRESS,
            "Content-Type", getContentType(fileExtension),
            "Connection", "close"
        );

        return new HttpResponse(startLine, header);
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

    private static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        log.info("fileName : {}", fileName);

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    private static String getHostAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
