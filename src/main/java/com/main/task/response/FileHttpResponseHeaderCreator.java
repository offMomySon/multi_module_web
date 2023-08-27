package com.main.task.response;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
        FileExtension foundExtension = FileExtension.find(fileExtension);
        return foundExtension.getContentType();
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

    private enum FileExtension {
        JPG("image/jpeg"),
        GIF("image/gif"),
        TXT("text/plain");

        private final String contentType;

        FileExtension(String contentType) {
            Objects.requireNonNull(contentType);
            if (contentType.isBlank()) {
                throw new RuntimeException("contentType is empty.");
            }
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }

        public static FileExtension find(String fileExtension) {
            return Arrays.stream(FileExtension.values())
                .filter(value -> value.name().equalsIgnoreCase(fileExtension))
                .findAny()
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Does not exist match contentType. Find contentType : `{}`", fileExtension)));
        }
    }
}
