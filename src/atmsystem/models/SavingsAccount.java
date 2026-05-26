package atmsystem.models;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.04;

    public SavingsAccount(String accountNumber, String customerId, double balance, String password) {
        super(accountNumber, customerId, balance, password);
    }

    public SavingsAccount(String accountNumber, String customerId, double balance,
                          double loanBalance, String password) {
        super(accountNumber, customerId, balance, loanBalance, password);
    }

    @Override
    public String getAccountType() { return "Savings"; }

    @Override
    public double getInterestRate() { return INTEREST_RATE; }
}
