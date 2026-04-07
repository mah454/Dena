package ir.moke.dena.http;

import com.sun.net.httpserver.HttpExchange;
import ir.moke.dena.jpms.ModuleContext;
import ir.moke.dena.jpms.ModuleController;
import ir.moke.dena.jpms.ModuleRepository;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class DenaResources {
    public static void list(HttpExchange exchange) {
        Set<ModuleContext> list = ModuleRepository.list();
        StringBuilder sb = new StringBuilder("[");
        list.stream()
                .peek(item -> sb.append(item.toJson()))
                .forEach(_ -> sb.append(","));
        sb.deleteCharAt(sb.length() - 1).append("]");
        int length = sb.length();
        try (OutputStream outputStream = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("content-type", "application/json");
            exchange.sendResponseHeaders(200, length);
            outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(HttpExchange exchange) {
        execute(exchange, ModuleController::load);
    }

    public static void unload(HttpExchange exchange) {
        execute(exchange, ModuleController::unload);
    }

    public static void stop(HttpExchange exchange) {
        execute(exchange, ModuleController::stop);
    }

    public static void start(HttpExchange exchange) {
        execute(exchange, ModuleController::start);
    }

    private static void execute(HttpExchange exchange, Consumer<String> controllerConsumer) {
        try {
            Path path = Path.of(exchange.getRequestURI().getPath());
            String moduleName = path.getFileName().toString();
            controllerConsumer.accept(moduleName);
            exchange.sendResponseHeaders(200, -1);
        } catch (Exception e) {
            try (OutputStream os = exchange.getResponseBody()) {
                String message = e.getMessage();
                exchange.sendResponseHeaders(500, message.length());
                os.write(message.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ignore) {
            }
        }
    }
}
