package com.main.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.util.IoUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class ObjectValueConverter implements ValueConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<?> convertClazz;

    public ObjectValueConverter(Class<?> convertClazz) {
        Objects.requireNonNull(convertClazz);
        this.convertClazz = convertClazz;
    }

    @Override
    public InputStream convertToInputStream(@NonNull Object object) {
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            return new ByteArrayInputStream(jsonString.getBytes(UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object convertToClazz(@NonNull String value) {
        Object convertedValue = value;
        if(String.class != convertClazz){
            convertedValue = readValue(value, convertClazz);
        }

        log.info("convertedValue : `{}`, convertClazz : {}", convertedValue, convertClazz);
        return convertedValue;
    }

    @Override
    public Object convertToClazz(@NonNull InputStream inputStream) {
        if(InputStream.class.isAssignableFrom(convertClazz)){
            return inputStream;
        }

        try {
            if(convertClazz == boolean.class || convertClazz == Boolean.class){
                DataInputStream dataInputStream = createDataInputStream(inputStream);
                return dataInputStream.readBoolean();
            }
            if(convertClazz == byte.class || convertClazz == Byte.class){
                return readAsByte(inputStream);
            }
            if(convertClazz == short.class || convertClazz == Short.class){
                DataInputStream dataInputStream = createDataInputStream(inputStream);
                return dataInputStream.readShort();
            }
            if(convertClazz == int.class || convertClazz == Integer.class){
                DataInputStream dataInputStream = createDataInputStream(inputStream);
                return dataInputStream.readInt();
            }
            if(convertClazz == long.class || convertClazz == Long.class){
                DataInputStream dataInputStream = createDataInputStream(inputStream);
                return dataInputStream.readLong();
            }
            if(convertClazz == float.class || convertClazz == Float.class){
                DataInputStream dataInputStream = createDataInputStream(inputStream);
                return dataInputStream.readFloat();
            }
            if(convertClazz == double.class || convertClazz == Double.class){
                DataInputStream dataInputStream = createDataInputStream(inputStream);
                return dataInputStream.readDouble();
            }

            return objectMapper.readValue(inputStream, convertClazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataInputStream createDataInputStream(InputStream inputStream) {
        inputStream = IoUtils.createBufferedInputStream(inputStream);
        return new DataInputStream(inputStream);
    }

    private static byte[] readAsByte(InputStream inputStream){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // Buffer to read data from InputStream
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read from the InputStream and write to the ByteArrayOutputStream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Get the byte array from the ByteArrayOutputStream
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the InputStream if needed
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static Object readValue(String value, Class<?> convertClazz){
        try {
            return objectMapper.readValue(value, convertClazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
