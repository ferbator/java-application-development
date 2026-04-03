package itmo.course.lab1.transaction;

import itmo.course.lab1.account.AbstractAccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WithdrawalTransaction extends AbstractTransaction {
    private final AbstractAccount account;

    public WithdrawalTransaction(AbstractAccount account, BigDecimal amount) {
        super(amount);
        this.account = account;
    }

    @Override
    public String getDescription() {
        return "Снятие со счета " + account.getId();
    }

    @Override
    protected void doExecute(LocalDate currentDate) {
        account.withdraw(getAmount(), currentDate);
    }

    @Override
    protected void doCancel(LocalDate currentDate) {
        account.deposit(getAmount());
    }
}
