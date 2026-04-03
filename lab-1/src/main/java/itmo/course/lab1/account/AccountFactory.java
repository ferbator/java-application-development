package itmo.course.lab1.account;

import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.client.Client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public interface AccountFactory {
    DebitAccount createDebitAccount(Bank bank, Client client);

    CreditAccount createCreditAccount(Bank bank, Client client);

    DepositAccount createDepositAccount(Bank bank, Client client, BigDecimal initialDeposit, LocalDate openedAt, Period term);
}
