package com.main.util.converter;

import com.main.util.IoUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.NonNull;
import static com.main.util.IoUtils.createBufferedInputStream;

public class FileValueConverter implements ValueConverter {
    @Override
    public InputStream convertToInputStream(@NonNull Object file) {
        return doConvertToInputStream((File) file);
    }

    @Override
    public Object convertToClazz(@NonNull String value) {
        return new File(value);
    }

    @Override
    public Object convertToClazz(@NonNull InputStream inputStream) {
        throw new RuntimeException("does not support.");
    }

    public File convertToFile(@NonNull InputStream inputStream, @NonNull String filePath) {
        BufferedInputStream bufferedInputStream = createBufferedInputStream(inputStream);
        File outputFile = new File(filePath);

        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = bufferedInputStream.read()) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return outputFile;
    }

    private InputStream doConvertToInputStream(File fIle) {
        try {
            return createBufferedInputStream(new FileInputStream(fIle));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
