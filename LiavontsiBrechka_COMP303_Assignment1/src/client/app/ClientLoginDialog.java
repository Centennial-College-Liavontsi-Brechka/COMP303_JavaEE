package client.app;

import util.InputReader;
import util.NetworkCommands;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;

public class ClientLoginDialog extends JDialog {
    private JTextField accountNumberTextField;
    private JTextField pinTextField;
    private JLabel accountNumberLabel;
    private JLabel pinLabel;
    private JButton loginButton;
    private JButton cancelButton;
    private JPanel buttonsPanel;
    private JPanel mainPanel;
    private JFrame parent;

    private InputReader input;
    private PrintWriter output;

    public ClientLoginDialog(JFrame parent, boolean modal, InputReader input, PrintWriter output) {
        super(parent, modal);
        this.parent = parent;

        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        loginButton.addActionListener(e -> login());
        cancelButton.addActionListener((e -> exit()));

        this.input = input;
        this.output = output;
    }

    private void login() {
        try {
            int accountNumber = Integer.parseInt(accountNumberTextField.getText());
            int pin = Integer.parseInt(pinTextField.getText());
            output.printf("%s %d %d\n", NetworkCommands.LOGIN, accountNumber, pin);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please, fill in both fields (Account Number and PIN) with integer values.",
                    "Login error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (input.hasNext()) {
            String nextToken = input.next();
            if (nextToken.equals(NetworkCommands.CONFIRM_AUTHORIZATION.name())) {
                this.setVisible(false);
                this.parent.setVisible(true);
                JOptionPane.showMessageDialog(this,
                        "You have logged in successfully.",
                        "Login successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (nextToken.equals(NetworkCommands.REJECT_AUTHORIZATION.name())) {
                JOptionPane.showMessageDialog(this,
                        "Account number or password is incorrect.\nPlease, try again.",
                        "Login error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Something went wrong on the server. Please, try again later.",
                        "Server error",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void exit() {
        setVisible(false);
        parent.dispose();
        System.exit(0);
    }
}
