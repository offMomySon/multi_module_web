package com.main.util.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import static com.main.util.IoUtils.createBufferedInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;

public class InputStreamValueConverter implements ValueConverter {
    @Override
    public InputStream convertToInputStream(Object object) {
        Objects.requireNonNull(object);
        return convertToInputStream((InputStream) object);
    }

    @Override
    public Object convertToClazz(String value) {
        Objects.requireNonNull(value);
        return new ByteArrayInputStream(value.getBytes(UTF_8));
    }

    public InputStream convertToInputStream(InputStream inputStream) {
        return createBufferedInputStream(inputStream);
    }
}
