package ir.moke.dena.console;

import org.jline.console.CommandInput;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.io.PrintWriter;

public class ConsoleUtils {
    public static void println(CommandInput input, String message) {
        try (Terminal terminal = input.terminal()) {
            PrintWriter writer = terminal.writer();
            writer.println(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
