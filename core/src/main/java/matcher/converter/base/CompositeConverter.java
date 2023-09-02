package matcher.converter.base;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CompositeConverter implements Converter<Object> {
    private static final Map<Class<?>, Converter<?>> _converters;

    private static final EmptyConverter emptyConverter = new EmptyConverter();

    static {
        ObjectConverter objectConverter = new ObjectConverter();

        Map<Class<?>, Converter<?>> converters = new HashMap<>();

        converters.put(Path.class, new PathConverter());
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

        _converters = Map.copyOf(converters);
    }

    @Override
    public InputStream convertToInputStream(Object object) {
        Class<?> key = object.getClass();

        Optional<Class<?>> foundkey = _converters.keySet().stream()
            .filter(e -> e.isAssignableFrom(key))
            .findFirst();

        if(foundkey.isEmpty()){
            return emptyConverter.convertToInputStream(object);
        }

        Class<?> targetClazz = foundkey.get();
        Converter<Object> converter = (Converter<Object>) _converters.get(targetClazz);
        return converter.convertToInputStream(object);

//        Converter<Object> converter = (Converter<Object>) first;
//            .map(_converters::get)
//            .orElse(emptyConverter);

//        return converter.convertToInputStream(object);
    }
}
