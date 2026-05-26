package atmsystem.gui;

import atmsystem.models.Bank;

public class BankingSystemGUI extends LoginGUI {
    private final Bank bank;

    public BankingSystemGUI(Bank bank) {
        super();
        this.bank = bank;
        setTitle(bank.getBankName() + " - Login");
    }

    public Bank getBank() {
        return bank;
    }
}
