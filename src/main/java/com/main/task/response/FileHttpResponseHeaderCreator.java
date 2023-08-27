package com.main.task.response;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileHttpResponseHeaderCreator extends HttpResponseHeaderCreator {
    private final Path path;

    public FileHttpResponseHeaderCreator(SimpleDateFormat simpleDateFormat, String hostAddress, Path path) {
        super(simpleDateFormat, hostAddress);

        Objects.requireNonNull(path);
        this.path = path;
    }


    @Override
    public String extractContentType() {
        String fileExtension = getFileExtension();
        return doExtractContentType(fileExtension);
    }

    private static String doExtractContentType(String fileExtension) {
        log.info("fileExtension : {}", fileExtension);

        switch (fileExtension) {
            case "jpg":
                return "image/jpeg";
            case "txt":
                return "text/html; charset=UTF-8";
        }
        throw new RuntimeException("does exist match fileExtension");
    }

    private String getFileExtension() {
        String fileName = path.getFileName().toString();
        log.info("fileName : {}", fileName);

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
