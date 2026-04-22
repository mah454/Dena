package ir.moke.dena.utils;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Logo {
    public static void print() {
        try {
            Path logoPath = Path.of(System.getProperty("dena.work-dir")).resolve("conf/logo");
            if (FileUtils.isFileExists(logoPath)) {
                print(logoPath.toUri().toURL());
            } else {
                try (Stream<URL> stream = Thread.currentThread().getContextClassLoader().resources("logo")) {
                    stream.filter(item -> item.getFile().contains("dena-runtime"))
                            .findFirst()
                            .ifPresent(Logo::print);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void print(URL url) {
        try (InputStream inputStream = url.openStream()) {
            byte[] bytes = inputStream.readAllBytes();
            System.out.write(bytes);
            System.out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
