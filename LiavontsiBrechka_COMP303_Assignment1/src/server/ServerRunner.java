package server;

import server.atm.ATMServer;

public class ServerRunner {
    private static final int PORT = 8088;

    public static void main(String[] args) {
        ATMServer atmServer = new ATMServer(PORT);
        atmServer.startServer();
    }
}
