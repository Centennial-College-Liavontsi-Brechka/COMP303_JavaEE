package server.atm;

public class BankDatabase {
    private Account[] accounts;

    public BankDatabase() {
        this.accounts = new Account[]{
                new Account(123456, 1234, 1200.0D),
                new Account(654321, 4321, 4213.0D)
        };
    }

    public BankDatabase(Account[] accounts) {
        this.accounts = accounts;
    }

    public double getTotalBalance(int userAccountNumber) throws NoSuchAccountInDatabaseException {
        return getAccount(userAccountNumber).getTotalBalance();
    }

    public void deposit(int userAccountNumber, double amount) throws NoSuchAccountInDatabaseException {
        getAccount(userAccountNumber).deposit(amount);
    }

    public void withdraw(int userAccountNumber, double amount)
            throws NoSuchAccountInDatabaseException, Account.NotEnoughMoneyException {
        getAccount(userAccountNumber).withdraw(amount);
    }

    public boolean authenticateUser(int userAccountNumber, int userPIN) {
        try {
            return getAccount(userAccountNumber).validatePIN(userPIN);
        } catch (NoSuchAccountInDatabaseException e) {
            return false;
        }
    }

    public Account getAccount(int accountNumber) throws NoSuchAccountInDatabaseException {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) return account;
        }

        throw new NoSuchAccountInDatabaseException();
    }

    public static class NoSuchAccountInDatabaseException extends Exception {
        public NoSuchAccountInDatabaseException() {
            super("No such account in database was found.");
        }

        public NoSuchAccountInDatabaseException(String message) {
            super(message);
        }
    }
}
