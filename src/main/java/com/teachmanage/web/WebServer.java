package com.teachmanage.web;

import com.teachmanage.server.service.TeachingService;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private final HttpServer server;

    public WebServer(int port, TeachingService service) throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(8));
        this.server.createContext("/api/", new ApiHandler(service));
        this.server.createContext("/", new StaticFileHandler());
    }

    public void start() {
        server.start();
        log.info("Web server listening on http://localhost:{}", server.getAddress().getPort());
    }

    public void stop() {
        server.stop(1);
    }
}
