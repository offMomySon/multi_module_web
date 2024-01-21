package com.main.util.converter;

import java.io.InputStream;
import lombok.NonNull;

public interface ValueConverter {
    InputStream convertToInputStream(@NonNull Object object);

    Object convertToClazz(@NonNull String value);

    Object convertToClazz(@NonNull InputStream inputStream);
}
