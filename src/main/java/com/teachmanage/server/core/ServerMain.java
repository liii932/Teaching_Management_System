package com.teachmanage.server.core;

import com.teachmanage.server.dao.*;
import com.teachmanage.server.service.TeachingService;
import com.teachmanage.web.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class ServerMain {
    private static final Logger log = LoggerFactory.getLogger(ServerMain.class);

    private Connection connection;
    private WebServer webServer;

    public void start() throws Exception {
        DbConfig config = DbConfig.load();
        this.connection = new DbManager(config).open();
        int serverPort = config.serverPort();
        log.info("Using web server port {}", serverPort);

        TeachingService service = new TeachingService(
                new DepartmentDao(connection),
                new MajorDao(connection),
                new ClassDao(connection),
                new StudentDao(connection),
                new TeacherDao(connection),
                new CourseDao(connection),
                new ScoreDao(connection)
        );

        this.webServer = new WebServer(serverPort, service);
        this.webServer.start();
        log.info("Teaching Management System started at http://localhost:{}", serverPort);
    }

    public void stop() {
        if (webServer != null) {
            webServer.stop();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ignored) {
        }
    }
}
