package atmsystem.models;

public class FixedDepositAccount extends Account {
    private static final double INTEREST_RATE = 0.07;
    private static final int LOCK_PERIOD_MONTHS = 12;

    public FixedDepositAccount(String accountNumber, String customerId, double balance, String password) {
        super(accountNumber, customerId, balance, password);
    }

    public FixedDepositAccount(String accountNumber, String customerId, double balance,
                               double loanBalance, String password) {
        super(accountNumber, customerId, balance, loanBalance, password);
    }

    @Override
    public String getAccountType() {
        return "Fixed Deposit";
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }

    public int getLockPeriodMonths() {
        return LOCK_PERIOD_MONTHS;
    }
}
