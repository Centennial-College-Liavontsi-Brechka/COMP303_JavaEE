package client;

import client.app.ATMClient;

public class ClientRunner {
    private static final String HOST = "localhost";
    private static final int PORT = 8088;

    public static void main(String[] args) {
        ATMClient atmClient = new ATMClient(HOST, PORT);
        atmClient.startApplication();
    }
}
