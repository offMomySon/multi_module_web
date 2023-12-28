package com.main.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import static java.util.Objects.isNull;


/**
 * 루틴클래스.
 * 역할.
 * 파일 시스템에서 특정 package 하위에 존재하는 class 파일들을 찾는 역할.
 * 루틴.
 * 1. 특정 path 의 하위의 모든 파일을 찾는다.
 * 2. 일반파일 여부를 확인한다.
 * 3. .class 확장자를 가지고 있는지 확인한다.
 * 4. root path 와 file path 를 가지고 class 를 생성한다.
 */
@Slf4j
public class FileSystemUtil {
    private static final String JAR_SCHEME = "jar";

    public static Path getClazzRootPath(@NonNull Class<?> clazz) {
        URI classDirectoryUri = getResourceUri(clazz, "");

        if (jarFileSystem(classDirectoryUri)) {
            try (FileSystem jarFileSystem = FileSystems.newFileSystem(classDirectoryUri, Collections.emptyMap())) {
                Path rootPath = jarFileSystem.getPath("/");
                log.info("[jarfileSystem] rootPath : {}", rootPath);
                return rootPath;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        URI rootUri = getResourceUri(clazz, "/");
        Path rootPath = Paths.get(rootUri);
        log.info("rootPath : {}", rootPath);
        return rootPath;
    }

    private static URI getResourceUri(Class<?> clazz, String path) {
        URL pathUrl = clazz.getResource(path);
        if (isNull(pathUrl)) {
            throw new RuntimeException("url is null.");
        }

        URI uri = convertUrlIntoUrI(pathUrl);
        log.info("uri : {}", uri);
        return uri;
    }

    private static URI convertUrlIntoUrI(URL classDirectoryUrl) {
        try {
            return classDirectoryUrl.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean jarFileSystem(URI uri) {
        String scheme = uri.getScheme();
        log.info("scheme : {}", scheme);
        return JAR_SCHEME.equalsIgnoreCase(scheme);
    }
}
