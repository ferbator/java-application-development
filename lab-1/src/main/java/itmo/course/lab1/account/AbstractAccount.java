package itmo.course.lab1.account;

import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.AccountOperationException;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public abstract class AbstractAccount {
    private final UUID id;
    private final Bank bank;
    private final Client owner;
    private final AccountType type;
    private BigDecimal balance;
    private BigDecimal pendingInterest;

    protected AbstractAccount(Bank bank, Client owner, AccountType type) {
        this.id = UUID.randomUUID();
        this.bank = bank;
        this.owner = owner;
        this.type = type;
        this.balance = BigDecimal.ZERO.setScale(2);
        this.pendingInterest = BigDecimal.ZERO.setScale(10);
    }

    public UUID getId() {
        return id;
    }

    public Bank getBank() {
        return bank;
    }

    public Client getOwner() {
        return owner;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getPendingInterest() {
        return pendingInterest;
    }

    public void deposit(BigDecimal amount) {
        BigDecimal normalizedAmount = MoneyUtils.requirePositive(amount, "Сумма пополнения должна быть больше нуля");
        changeBalance(normalizedAmount);
    }

    public void withdraw(BigDecimal amount, LocalDate currentDate) {
        withdraw(amount, currentDate, false);
    }

    public void withdraw(BigDecimal amount, LocalDate currentDate, boolean bypassRestrictions) {
        BigDecimal normalizedAmount = MoneyUtils.requirePositive(amount, "Сумма снятия должна быть больше нуля");
        ensureSuspiciousClientLimit(normalizedAmount, bypassRestrictions);
        ensureWithdrawalAllowed(normalizedAmount, currentDate, bypassRestrictions);
        changeBalance(normalizedAmount.negate());
    }

    public void onDayPassed(LocalDate currentDate) {
        BigDecimal annualRate = getAnnualInterestRate();
        if (annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal dailyInterest = MoneyUtils.calculateDailyInterest(balance, annualRate);
        pendingInterest = MoneyUtils.normalizeInternal(pendingInterest.add(dailyInterest));
    }

    public void onMonthEnd(LocalDate currentDate) {
        if (pendingInterest.compareTo(BigDecimal.ZERO) > 0) {
            changeBalance(pendingInterest);
            pendingInterest = BigDecimal.ZERO.setScale(10);
        }

        applyMonthEndCharges(currentDate);
    }

    protected void changeBalance(BigDecimal delta) {
        balance = MoneyUtils.normalize(balance.add(delta));
    }

    private void ensureSuspiciousClientLimit(BigDecimal amount, boolean bypassRestrictions) {
        if (bypassRestrictions) {
            return;
        }

        if (owner.isSuspicious()
                && amount.compareTo(bank.getSettings().getSuspiciousClientLimit()) > 0) {
            throw new AccountOperationException(
                    "Сомнительный клиент не может снять или перевести больше "
                            + MoneyUtils.format(bank.getSettings().getSuspiciousClientLimit())
            );
        }
    }

    protected abstract void ensureWithdrawalAllowed(BigDecimal amount, LocalDate currentDate, boolean bypassRestrictions);

    protected abstract BigDecimal getAnnualInterestRate();

    protected void applyMonthEndCharges(LocalDate currentDate) {
    }
}
