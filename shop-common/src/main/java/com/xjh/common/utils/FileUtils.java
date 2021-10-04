package com.xjh.common.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileUtils {
    public static String readFile(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
            stream.forEach(s -> sb.append(s).append(" "));
        }
        return sb.toString();
    }
}
