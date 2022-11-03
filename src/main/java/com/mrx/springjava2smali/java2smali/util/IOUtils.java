package com.mrx.springjava2smali.java2smali.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtils {

    public static void writeToFile(String file, byte[] content) {
        try (OutputStream ous = Files.newOutputStream(Path.of(file))) {
            ous.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readBytes(String file) {
        try {
            return Files.readAllBytes(Path.of(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
