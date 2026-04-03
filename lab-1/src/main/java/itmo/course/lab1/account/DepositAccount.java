package itmo.course.lab1.account;

import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.AccountOperationException;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepositAccount extends AbstractAccount {
    private final BigDecimal annualInterestRate;
    private final LocalDate maturityDate;

    public DepositAccount(Bank bank, Client owner, BigDecimal annualInterestRate, LocalDate maturityDate) {
        super(bank, owner, AccountType.DEPOSIT);
        this.annualInterestRate = MoneyUtils.requireNonNegative(
                annualInterestRate,
                "Ставка по депозиту не может быть отрицательной"
        );
        this.maturityDate = maturityDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public BigDecimal getFixedAnnualInterestRate() {
        return annualInterestRate;
    }

    @Override
    protected void ensureWithdrawalAllowed(BigDecimal amount, LocalDate currentDate, boolean bypassRestrictions) {
        if (!bypassRestrictions && currentDate.isBefore(maturityDate)) {
            throw new AccountOperationException("С депозита нельзя снимать деньги до окончания срока");
        }

        if (getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountOperationException("На депозите недостаточно средств");
        }
    }

    @Override
    protected BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }
}
