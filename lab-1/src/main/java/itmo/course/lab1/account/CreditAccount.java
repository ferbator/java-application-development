package itmo.course.lab1.account;

import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.AccountOperationException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditAccount extends AbstractAccount {
    public CreditAccount(Bank bank, Client owner) {
        super(bank, owner, AccountType.CREDIT);
    }

    @Override
    protected void ensureWithdrawalAllowed(BigDecimal amount, LocalDate currentDate, boolean bypassRestrictions) {
        BigDecimal allowedMinimum = getBank().getSettings().getCreditLimit().negate();
        if (getBalance().subtract(amount).compareTo(allowedMinimum) < 0) {
            throw new AccountOperationException("Превышен кредитный лимит");
        }
    }

    @Override
    protected BigDecimal getAnnualInterestRate() {
        return BigDecimal.ZERO.setScale(2);
    }

    @Override
    protected void applyMonthEndCharges(LocalDate currentDate) {
        if (getBalance().compareTo(BigDecimal.ZERO) < 0) {
            changeBalance(getBank().getSettings().getCreditNegativeBalanceCommission().negate());
        }
    }
}
