package itmo.course.lab1.bank;

import itmo.course.lab1.exception.ValidationException;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;

public class BankSettings {
    private BigDecimal debitAnnualInterestRate;
    private BigDecimal suspiciousClientLimit;
    private BigDecimal creditLimit;
    private BigDecimal creditNegativeBalanceCommission;
    private DepositRatePolicy depositRatePolicy;

    public BankSettings(
            BigDecimal debitAnnualInterestRate,
            BigDecimal suspiciousClientLimit,
            BigDecimal creditLimit,
            BigDecimal creditNegativeBalanceCommission,
            DepositRatePolicy depositRatePolicy
    ) {
        setDebitAnnualInterestRate(debitAnnualInterestRate);
        setSuspiciousClientLimit(suspiciousClientLimit);
        setCreditLimit(creditLimit);
        setCreditNegativeBalanceCommission(creditNegativeBalanceCommission);
        setDepositRatePolicy(depositRatePolicy);
    }

    public static BankSettings defaultSettings() {
        return new BankSettings(
                new BigDecimal("3.65"),
                new BigDecimal("1000"),
                new BigDecimal("10000"),
                new BigDecimal("500"),
                DepositRatePolicy.defaultPolicy()
        );
    }

    public BigDecimal getDebitAnnualInterestRate() {
        return debitAnnualInterestRate;
    }

    public void setDebitAnnualInterestRate(BigDecimal debitAnnualInterestRate) {
        this.debitAnnualInterestRate = MoneyUtils.requireNonNegative(
                debitAnnualInterestRate,
                "Процент по дебетовому счету не может быть отрицательным"
        );
    }

    public BigDecimal getSuspiciousClientLimit() {
        return suspiciousClientLimit;
    }

    public void setSuspiciousClientLimit(BigDecimal suspiciousClientLimit) {
        this.suspiciousClientLimit = MoneyUtils.requirePositive(
                suspiciousClientLimit,
                "Лимит для сомнительных клиентов должен быть положительным"
        );
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = MoneyUtils.requirePositive(creditLimit, "Кредитный лимит должен быть положительным");
    }

    public BigDecimal getCreditNegativeBalanceCommission() {
        return creditNegativeBalanceCommission;
    }

    public void setCreditNegativeBalanceCommission(BigDecimal creditNegativeBalanceCommission) {
        this.creditNegativeBalanceCommission = MoneyUtils.requireNonNegative(
                creditNegativeBalanceCommission,
                "Комиссия не может быть отрицательной"
        );
    }

    public DepositRatePolicy getDepositRatePolicy() {
        return depositRatePolicy;
    }

    public void setDepositRatePolicy(DepositRatePolicy depositRatePolicy) {
        if (depositRatePolicy == null) {
            throw new ValidationException("Политика ставок по депозиту обязательна");
        }

        this.depositRatePolicy = depositRatePolicy;
    }
}
