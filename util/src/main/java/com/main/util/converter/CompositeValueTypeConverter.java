package com.main.util.converter;

import com.main.util.IoUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.NonNull;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CompositeValueTypeConverter {
    private static final String EMPTY_VALUE = "";
    private static final Map<Class<?>, ValueConverter> CONVERTERS_MAP;

    static {
        Map<Class<?>, ValueConverter> converters = new HashMap<>();
        // specific type.
        converters.put(Path.class, new PathValueConverter());
        converters.put(File.class, new FileValueConverter());

        // generic type.
        converters.put(boolean.class, new ObjectValueConverter(Boolean.class));
        converters.put(Boolean.class, new ObjectValueConverter(Boolean.class));
        converters.put(byte.class, new ObjectValueConverter(Byte.class));
        converters.put(Byte.class, new ObjectValueConverter(Byte.class));
        converters.put(char.class, new ObjectValueConverter(Character.class));
        converters.put(Character.class, new ObjectValueConverter(Character.class));
        converters.put(short.class, new ObjectValueConverter(Short.class));
        converters.put(Short.class, new ObjectValueConverter(Short.class));
        converters.put(int.class, new ObjectValueConverter(Integer.class));
        converters.put(Integer.class, new ObjectValueConverter(Integer.class));
        converters.put(long.class, new ObjectValueConverter(Long.class));
        converters.put(Long.class, new ObjectValueConverter(Long.class));
        converters.put(float.class, new ObjectValueConverter(Float.class));
        converters.put(Float.class, new ObjectValueConverter(Float.class));
        converters.put(double.class, new ObjectValueConverter(Double.class));
        converters.put(Double.class, new ObjectValueConverter(Double.class));
        converters.put(String.class, new ObjectValueConverter(String.class));

        CONVERTERS_MAP = Map.copyOf(converters);
    }

    public InputStream convertToInputStream(@NonNull Object object) {
        Class<?> convertClazz = object.getClass();
        ValueConverter foundValueConverter = findConverterOrDefault(convertClazz, objectConverterSupplier(convertClazz));
        return foundValueConverter.convertToInputStream(object);
    }

    public static Object convertToClazz(@NonNull InputStream inputStream, @NonNull Class<?> convertClazz) {
        ValueConverter foundValueConverter = findConverterOrDefault(convertClazz, objectConverterSupplier(convertClazz));
        return foundValueConverter.convertToClazz(inputStream);
    }

    public static Object convertToClazz(@NonNull String value, @NonNull Class<?> convertClazz) {
        ValueConverter foundValueConverter = findConverterOrDefault(convertClazz, objectConverterSupplier(convertClazz));
        return foundValueConverter.convertToClazz(value);
    }

    private static ValueConverter findConverterOrDefault(Class<?> findClazz, Supplier<ValueConverter> converterSupplier) {
        return CONVERTERS_MAP.entrySet().stream()
            .filter(entry -> entry.getKey().isAssignableFrom(findClazz))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElseGet(converterSupplier);
    }

    // custom type.
    private static Supplier<ValueConverter> objectConverterSupplier(Class<?> convertClazz) {
        return () -> new ObjectValueConverter(convertClazz);
    }
}
