package ir.moke.dena.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.stream.Stream;

public class Logo {
    public static void print() {
        try (Stream<URL> stream = Thread.currentThread().getContextClassLoader().resources("logo")) {
            stream.filter(item -> item.getFile().contains("dena-runtime"))
                    .findFirst()
                    .ifPresent(Logo::print);
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
