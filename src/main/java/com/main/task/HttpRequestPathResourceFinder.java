package com.main.task;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import matcher.PackageResourceFinder;
import vo.HttpRequestPath;

public class HttpRequestPathResourceFinder {
    private final PackageResourceFinder packageResourceFinder;

    public HttpRequestPathResourceFinder(PackageResourceFinder packageResourceFinder) {
        Objects.requireNonNull(packageResourceFinder);
        this.packageResourceFinder = packageResourceFinder;
    }

    public Optional<Path> findResource(HttpRequestPath httpRequestPath) {
        Path requestPath = httpRequestPath.getValue();
        return packageResourceFinder.findResource(requestPath);
    }
}
