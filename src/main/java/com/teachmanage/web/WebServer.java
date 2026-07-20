package com.teachmanage.web;

import com.teachmanage.server.service.TeachingService;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private final HttpServer server;

    public WebServer(int port, TeachingService service) throws Exception {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (BindException e) {
            throw new BindException("Port " + port + " is already in use. "
                    + "Stop the process using this port or set TMS_SERVER_PORT/server.port to another free port.");
        }
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
