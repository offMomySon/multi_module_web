package com.main;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class InstanceMethod {
    private final Object object;
    private final Method method;

    public InstanceMethod(@NonNull Object object, @NonNull Method method) {
        this.object = object;
        this.method = method;
    }
}
