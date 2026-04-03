package itmo.course.lab1.transaction;

import itmo.course.lab1.account.AbstractAccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepositTransaction extends AbstractTransaction {
    private final AbstractAccount account;

    public DepositTransaction(AbstractAccount account, BigDecimal amount) {
        super(amount);
        this.account = account;
    }

    @Override
    public String getDescription() {
        return "Пополнение счета " + account.getId();
    }

    @Override
    protected void doExecute(LocalDate currentDate) {
        account.deposit(getAmount());
    }

    @Override
    protected void doCancel(LocalDate currentDate) {
        account.withdraw(getAmount(), currentDate, true);
    }
}
