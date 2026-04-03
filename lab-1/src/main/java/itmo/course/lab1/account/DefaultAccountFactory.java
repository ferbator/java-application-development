package itmo.course.lab1.account;

import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.ValidationException;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class DefaultAccountFactory implements AccountFactory {
    @Override
    public DebitAccount createDebitAccount(Bank bank, Client client) {
        return new DebitAccount(bank, client);
    }

    @Override
    public CreditAccount createCreditAccount(Bank bank, Client client) {
        return new CreditAccount(bank, client);
    }

    @Override
    public DepositAccount createDepositAccount(Bank bank, Client client, BigDecimal initialDeposit, LocalDate openedAt, Period term) {
        MoneyUtils.requirePositive(initialDeposit, "Начальная сумма депозита должна быть больше нуля");
        if (term == null || term.isZero() || term.isNegative()) {
            throw new ValidationException("Срок депозита должен быть положительным");
        }

        BigDecimal annualRate = bank.getSettings().getDepositRatePolicy().findRate(initialDeposit);
        LocalDate maturityDate = openedAt.plus(term);
        return new DepositAccount(bank, client, annualRate, maturityDate);
    }
}
