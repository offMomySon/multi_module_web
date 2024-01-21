package com.main.util.converter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathValueConverter implements ValueConverter {

    @Override
    public InputStream convertToInputStream(@NonNull Object path) {
        return getPathInputStream((Path)path);
    }

    @Override
    public Object convertToClazz(@NonNull String value) {
        return Paths.get(value);
    }

    @Override
    public Object convertToClazz(@NonNull InputStream inputStream) {
        throw new RuntimeException("does not support.");
    }

    private static InputStream getPathInputStream(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
