package server.atm;

public class Account {
    private int accountNumber;
    private int pin;
    private double totalBalance;

    public Account(int accountNumber, int pin, double totalBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.totalBalance = totalBalance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public boolean validatePIN(int pin) {
        return this.pin == pin;
    }

    public void deposit(double amount) {
        this.totalBalance += amount;
    }

    public void withdraw(double amount) throws NotEnoughMoneyException {
        if (this.totalBalance < amount)
            throw new NotEnoughMoneyException();
        totalBalance -= amount;
    }


    public static class NotEnoughMoneyException extends Exception {
        public NotEnoughMoneyException() {
            super("Sorry, but you don't have enough available money on the account.");
        }

        public NotEnoughMoneyException(String message) {
            super(message);
        }
    }
}
