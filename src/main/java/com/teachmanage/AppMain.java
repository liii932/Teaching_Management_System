package com.teachmanage;

import com.teachmanage.server.core.ServerMain;

public class AppMain {
    public static void main(String[] args) {
        try {
            ServerMain server = new ServerMain();
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
