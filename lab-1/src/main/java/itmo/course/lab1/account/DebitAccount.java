package itmo.course.lab1.account;

import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.AccountOperationException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DebitAccount extends AbstractAccount {
    public DebitAccount(Bank bank, Client owner) {
        super(bank, owner, AccountType.DEBIT);
    }

    @Override
    protected void ensureWithdrawalAllowed(BigDecimal amount, LocalDate currentDate, boolean bypassRestrictions) {
        if (getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountOperationException("На дебетовом счете недостаточно средств");
        }
    }

    @Override
    protected BigDecimal getAnnualInterestRate() {
        return getBank().getSettings().getDebitAnnualInterestRate();
    }
}
