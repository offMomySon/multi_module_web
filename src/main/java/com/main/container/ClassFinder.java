package com.main.container;

import com.main.util.FileSystemUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassFinder {
    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String DIRECTORY_DELIMITER = "/";
    private static final String PACKAGE_DELIMITER = ".";

    private final Path classPackage;
    private final Path classPackageDirectory;

    public ClassFinder(Path classPackage, Path classPackageDirectory) {
        Objects.requireNonNull(classPackage);
        Objects.requireNonNull(classPackageDirectory);

        log.info("classPackage : {}", classPackage);
        log.info("classPackageDirectory : {}", classPackageDirectory);

        this.classPackage = classPackage.normalize();
        this.classPackageDirectory = classPackageDirectory.normalize();
    }

    public static ClassFinder from(Class<?> clazz, String classPackage) {
        Objects.requireNonNull(clazz);
        if (Objects.isNull(classPackage) || classPackage.isBlank()) {
            throw new RuntimeException("classPackage is null.");
        }
        
        Path clazzRootPath = FileSystemUtil.getClazzRootPath(clazz);
        Path newClassPackage = Path.of(classPackage.replace(PACKAGE_DELIMITER, DIRECTORY_DELIMITER));
        Path classPackageDirectory = clazzRootPath.resolve(newClassPackage);

        return new ClassFinder(newClassPackage, classPackageDirectory);
    }

    public List<Class<?>> findClazzes() {
        try (Stream<Path> classPathStream = Files.walk(classPackageDirectory)) {
            return classPathStream
                .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .filter(ClassFinder::hasClassExtension)
                .map(classFilePath -> createFullyQualifiedClassName(classPackage, classPackageDirectory, classFilePath))
                .map(ClassFinder::createClass)
                .collect(Collectors.toUnmodifiableList());
        } catch (IOException e){
            throw new RuntimeException(MessageFormat.format("io exception. {}", e.getMessage()));
        }
    }

    private static boolean hasClassExtension(Path fileName) {
        return fileName.getFileName().toString().endsWith(CLASS_FILE_EXTENSION);
    }

    private static String createFullyQualifiedClassName(Path classPackage, Path findClassPackageDirectory, Path classFilePath) {
        Path fullClassFilePath = findClassPackageDirectory.relativize(classFilePath);
        Path jvmFilePath = classPackage.resolve(fullClassFilePath);

        return jvmFilePath.toString()
            .substring(0, jvmFilePath.toString().lastIndexOf(CLASS_FILE_EXTENSION))
            .replace(DIRECTORY_DELIMITER, PACKAGE_DELIMITER);
    }

    private static Class<?> createClass(String fullyQualifiedClassName) {
        try {
            return Class.forName(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
