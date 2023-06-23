package com.main.matcher.converter.base;

import com.main.util.IoUtils;
import java.io.InputStream;
import java.util.Objects;

public class InputStreamConverter implements Converter {
    @Override
    public InputStream convertToInputStream(Object object) {
        Objects.requireNonNull(object);
        return convertToInputStream((InputStream) object);
    }

    public InputStream convertToInputStream(InputStream inputStream) {
        return IoUtils.createBufferedInputStream(inputStream);
    }
}
