package ir.moke.dena.utils;

import java.io.IOException;
import java.io.InputStream;

public class Logo {
    public static void print() {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("dena.logo")) {
            if (inputStream != null) {
                byte[] bytes = inputStream.readAllBytes();
                System.out.println(new String(bytes));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
