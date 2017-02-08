package client.app;

import util.InputReader;
import util.NetworkCommands;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ATMClient extends JFrame {
    private JPanel mainPanel;
    private JTextField moneyAmountTextField;
    private JComboBox operationComboBox;
    private JButton executionButton;
    private JButton logOutButton;
    private JLabel moneyAmountLabel;
    private JLabel operationLabel;
    private JPanel buttonsPanel;

    private ClientLoginDialog clientLoginDialog;

    private String host;
    private Integer port;
    private InputReader input;
    private PrintWriter output;

    public ATMClient(String host, int port) {
        super("ATM Application");

        this.host = host;
        this.port = port;

        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(false);
        setResizable(false);

        executionButton.addActionListener(e -> executeOperation());
        logOutButton.addActionListener(e -> logOut());
    }

    public void startApplication() {
        try {
            Socket connectionSocket = new Socket(InetAddress.getByName(host), port);
            input = new InputReader(connectionSocket.getInputStream());
            output = new PrintWriter(connectionSocket.getOutputStream(), true);
            clientLoginDialog = new ClientLoginDialog(this, true, input, output);
            clientLoginDialog.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void executeOperation() {
        String operation = String.valueOf(operationComboBox.getSelectedItem()).trim().toUpperCase();
        double moneyAmount;

        try {
            moneyAmount = Double.valueOf(moneyAmountTextField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please, fill in Money Amount field with integer value.",
                    "Operation error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (operation.equals("DEPOSIT")) deposit(moneyAmount);
        else if (operation.equals("WITHDRAW")) withdraw(moneyAmount);
    }

    private void deposit(double moneyAmount) {
        output.printf("%s %f\n", NetworkCommands.DEPOSIT, moneyAmount);

        if (input.hasNext()) {
            String nextToken = input.next();

            if (nextToken.equals(NetworkCommands.CONFIRM_DEPOSIT.name())) {
                JOptionPane.showMessageDialog(this,
                        String.format(
                                "You have deposited money successfully. Your current balance is $%.2f",
                                input.nextDouble()),
                        "Deposit successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (nextToken.equals(NetworkCommands.REJECT_DEPOSIT.name())) {
                JOptionPane.showMessageDialog(this,
                        "Deposit cannot be made at this time. Please, try again later.",
                        "Deposit error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Something went wrong on the server. Please, try again later.",
                        "Server error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void withdraw(double moneyAmount) {
        output.printf("%s %f\n", NetworkCommands.WITHDRAW, moneyAmount);

        if (input.hasNext()) {
            String nextToken = input.next();

            if (nextToken.equals(NetworkCommands.CONFIRM_WITHDRAWAL.name())) {
                JOptionPane.showMessageDialog(this,
                        String.format(
                                "You withdrawn money successfully. Your current balance is $%.2f",
                                input.nextDouble()),
                        "Withdraw successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (nextToken.equals(NetworkCommands.REJECT_WITHDRAWAL.name())) {
                JOptionPane.showMessageDialog(this,
                        String.format("Withdrawal cannot be made at this time:\n%s\nPlease, try again later.",
                                input.readLine()),
                        "Withdrawal error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Something went wrong on the server. Please, try again later.",
                        "Server error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logOut() {
        output.println(NetworkCommands.LOGOUT);

        this.setVisible(false);
        clientLoginDialog.setVisible(true);
    }
}
