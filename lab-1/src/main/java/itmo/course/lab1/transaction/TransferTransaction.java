package itmo.course.lab1.transaction;

import itmo.course.lab1.account.AbstractAccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferTransaction extends AbstractTransaction {
    private final AbstractAccount sourceAccount;
    private final AbstractAccount targetAccount;

    public TransferTransaction(AbstractAccount sourceAccount, AbstractAccount targetAccount, BigDecimal amount) {
        super(amount);
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
    }

    @Override
    public String getDescription() {
        return "Перевод со счета " + sourceAccount.getId() + " на счет " + targetAccount.getId();
    }

    @Override
    protected void doExecute(LocalDate currentDate) {
        sourceAccount.withdraw(getAmount(), currentDate);
        targetAccount.deposit(getAmount());
    }

    @Override
    protected void doCancel(LocalDate currentDate) {
        targetAccount.withdraw(getAmount(), currentDate, true);
        sourceAccount.deposit(getAmount());
    }
}
