package ir.moke.dena.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class FileUtils {
    public static boolean isFileExists(Path path) {
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }

    public static boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    public static void createDirectory(Path path) {
        try {
            if (!isFileExists(path)) Files.createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
