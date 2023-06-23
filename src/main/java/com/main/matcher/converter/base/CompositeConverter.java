package com.main.matcher.converter.base;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CompositeConverter implements Converter {
    private static final Map<Class<?>, Converter> converters = new HashMap<>();

    private static final EmptyConverter emptyConverter = new EmptyConverter();

    static {
        ObjectConverter objectConverter = new ObjectConverter();

        converters.put(InputStream.class, new InputStreamConverter());
        converters.put(File.class, new FileConverter());
        converters.put(boolean.class, objectConverter);
        converters.put(Boolean.class, objectConverter);
        converters.put(byte.class, objectConverter);
        converters.put(Byte.class, objectConverter);
        converters.put(char.class, objectConverter);
        converters.put(Character.class, objectConverter);
        converters.put(short.class, objectConverter);
        converters.put(Short.class, objectConverter);
        converters.put(int.class, objectConverter);
        converters.put(Integer.class, objectConverter);
        converters.put(long.class, objectConverter);
        converters.put(Long.class, objectConverter);
        converters.put(float.class, objectConverter);
        converters.put(Float.class, objectConverter);
        converters.put(double.class, objectConverter);
        converters.put(Double.class, objectConverter);
        converters.put(String.class, objectConverter);
    }

    @Override
    public InputStream convertToInputStream(Object object) {
        Class<?> key = object.getClass();

        Converter converter = converters.getOrDefault(key, emptyConverter);

        return converter.convertToInputStream(object);
    }
}
