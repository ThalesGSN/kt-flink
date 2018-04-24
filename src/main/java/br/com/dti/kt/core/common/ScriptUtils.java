package br.com.dti.kt.core.common;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ScriptUtils {

    private ScriptUtils() {

    }

    public static String get(String baseDir, String path, String... paths) {
        return get(baseDir, Paths.get(path, paths));
    }

    public static String get(String baseDir, Path path) {
        return get(baseDir, path.toString());
    }

    public static String get(String baseDir, String path) {
        String filePath = Paths.get(baseDir, path).toString();
        InputStream is = ScriptUtils.class.getClassLoader().getResourceAsStream(filePath);
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            int result = bis.read();
            while (result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }
            return buf.toString("UTF-8");
        } catch (IOException e) {
            throw new IllegalArgumentException("read() of " + filePath + " failed." + e.getMessage(), e);
        } finally {
            try {
                buf.close();
            } catch (IOException e) {
                log.info("ByteArrayOutputStream couldn't be closed - " + e.getMessage(), e);
            }

            try {
                bis.close();
            } catch (IOException e) {
                log.info("BufferedInputStream couldn't be closed - " + e.getMessage(), e);
            }

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.info("InputStream couldn't be closed - " + e.getMessage(), e);
            }
        }
    }
}
