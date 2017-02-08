package server.atm;

import sun.nio.ch.Net;
import util.InputReader;
import util.NetworkCommands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ATMServer {
    private CashDispenser cashDispenser;
    private BankDatabase bankDatabase;
    private PrintWriter serverConsole;

    private final int port;
    private final ExecutorService executorService;

    public ATMServer(int port) {
        this.cashDispenser = new CashDispenser();
        this.bankDatabase = new BankDatabase();
        this.serverConsole = new PrintWriter(System.out, true);

        this.port = port;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverConsole.printf("Server is listening in port %d\n", port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                serverConsole.printf("%s is connected.\n", clientSocket.getInetAddress().getHostAddress());
                executorService.execute(new ATMClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private class ATMClient implements Runnable {
        private PrintWriter output;
        private InputReader input;
        private Socket clientSocket;

        private Integer clientAccountNumber;

        ATMClient(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                output = new PrintWriter(this.clientSocket.getOutputStream(), true);
                input = new InputReader(this.clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        @Override
        public void run() {
            try {
                String nextToken;
                while (input.hasNext()) {
                    nextToken = input.next();
                    if (nextToken.equals(NetworkCommands.LOGIN.name())) {
                        int accountNumber = input.nextInt();
                        int pin = input.nextInt();
                        if (bankDatabase.authenticateUser(accountNumber, pin)) {
                            clientAccountNumber = accountNumber;
                            output.println(NetworkCommands.CONFIRM_AUTHORIZATION);
                        } else output.println(NetworkCommands.REJECT_AUTHORIZATION);
                    } else if (nextToken.equals(NetworkCommands.LOGOUT.name())) {
                        clientAccountNumber = null;
                    } else if (nextToken.equals(NetworkCommands.DEPOSIT.name())) {
                        try {
                            double moneyToDeposit = input.nextDouble();
                            cashDispenser.collectCash((int) moneyToDeposit);
                            bankDatabase.deposit(clientAccountNumber, moneyToDeposit);
                            output.printf("%s %f\n",
                                    NetworkCommands.CONFIRM_DEPOSIT,
                                    bankDatabase.getTotalBalance(clientAccountNumber));
                        } catch (BankDatabase.NoSuchAccountInDatabaseException e) {
                            output.println(NetworkCommands.REJECT_DEPOSIT);
                            e.printStackTrace();
                        }
                    } else if (nextToken.equals(NetworkCommands.WITHDRAW.name())) {
                        try {
                            double moneyToWithdraw = input.nextDouble();
                            cashDispenser.dispenseCash((int) moneyToWithdraw);
                            bankDatabase.withdraw(clientAccountNumber, moneyToWithdraw);
                            output.printf("%s %f\n",
                                    NetworkCommands.CONFIRM_WITHDRAWAL,
                                    bankDatabase.getTotalBalance(clientAccountNumber));
                        } catch (CashDispenser.NoCashAvailableException |
                                Account.NotEnoughMoneyException |
                                BankDatabase.NoSuchAccountInDatabaseException e) {
                            output.printf("%s\n%s\n", NetworkCommands.REJECT_WITHDRAWAL, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                try {
                    clientSocket.close();
                    serverConsole.printf("%s is disconnected.\n", clientSocket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
