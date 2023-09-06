package matcher.converter.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        if (Objects.isNull(object)) {
            return new ByteArrayInputStream("".getBytes(UTF_8));
        }

        Class<?> clazz = object.getClass();
        Class<?> foundClazzKey = _converters.keySet().stream()
            .filter(e -> e.isAssignableFrom(clazz))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("does not exist convertable type."));

        Converter<Object> converter = (Converter<Object>) _converters.get(foundClazzKey);
        return converter.convertToInputStream(object);
    }
}
