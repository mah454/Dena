package ir.moke.dena.http;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiServer {
    private static final HttpServer server;

    static {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(ApiServer::triggerShutdownHook));
            server = HttpServer.create(new InetSocketAddress(2120), 0);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void triggerShutdownHook() {
        server.stop(0);
    }

    public static void start() {
        server.createContext("/api/dena/list", DenaResources::list);
        server.createContext("/api/dena/load", DenaResources::load);
        server.createContext("/api/dena/unload", DenaResources::unload);
        server.createContext("/api/dena/stop", DenaResources::stop);
        server.createContext("/api/dena/start", DenaResources::start);
    }
}
