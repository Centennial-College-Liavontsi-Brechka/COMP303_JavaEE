package server.atm;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CashDispenser {
    private static final int[] INITIAL_BILLS = new int[]{1, 5, 10, 20, 50, 100};
    private static final int INITIAL_BILL_COUNT = 1;
    private Map<Integer, Integer> bills;

    public CashDispenser() {
        bills = new TreeMap<>(Comparator.reverseOrder());
        for (Integer bill : INITIAL_BILLS) {
            bills.put(bill, INITIAL_BILL_COUNT);
        }
    }

    public void collectCash(int amount) {
        int currentCount;
        for (Integer bill : bills.keySet()) {
            currentCount = 0;
            while (amount >= bill && currentCount < bills.get(bill)) {
                amount -= bill;
                currentCount++;
            }
            bills.replace(bill, bills.get(bill) + currentCount);
        }
    }

    public void dispenseCash(int amount) throws NoCashAvailableException {
        if (!isSufficientCashAvailable(amount))
            throw new NoCashAvailableException();

        int currentCountNeeded;
        for (Integer bill : bills.keySet()) {
            currentCountNeeded = 0;
            while (amount >= bill && currentCountNeeded < bills.get(bill)) {
                amount -= bill;
                currentCountNeeded++;
            }
            bills.replace(bill, bills.get(bill) - currentCountNeeded);
        }
    }

    private boolean isSufficientCashAvailable(int amount) {
        int currentCountNeeded;
        for (Integer bill : bills.keySet()) {
            currentCountNeeded = 0;
            while (amount >= bill && currentCountNeeded < bills.get(bill)) {
                amount -= bill;
                currentCountNeeded++;
            }
        }

        return amount == 0;
    }

    public static class NoCashAvailableException extends Exception {
        public NoCashAvailableException() {
            super("Sorry, the ATMServer doesn't have bills to process the withdrawal.");
        }

        public NoCashAvailableException(String message) {
            super(message);
        }
    }
}
