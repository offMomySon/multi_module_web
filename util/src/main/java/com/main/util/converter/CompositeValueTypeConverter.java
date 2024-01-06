package com.main.util.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CompositeValueTypeConverter {
    private static final String EMPTY_VALUE = "";
    private static final Map<Class<?>, ValueConverter> CONVERTERS_MAP;

    static {
        Map<Class<?>, ValueConverter> converters = new HashMap<>();
        // specific type.
        converters.put(InputStream.class, new InputStreamValueConverter());
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

        CONVERTERS_MAP = converters.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    public InputStream convertToInputStream(Object object) {
        if (Objects.isNull(object)) {
            return new ByteArrayInputStream(EMPTY_VALUE.getBytes(UTF_8));
        }

        Class<?> convertClazz = object.getClass();
        ValueConverter foundValueConverter = findConverterOrDefault(convertClazz, objectConverterSupplier(convertClazz));
        return foundValueConverter.convertToInputStream(object);
    }

    public <T> T convertToClazz(String value, Class<T> convertClazz) {
        if (Objects.isNull(value) || Objects.isNull(convertClazz)) {
            throw new RuntimeException("Invalid value. value is emtpy.");
        }

        ValueConverter foundValueConverter = findConverterOrDefault(convertClazz, objectConverterSupplier(convertClazz));
        return (T) foundValueConverter.convertToClazz(value);
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
