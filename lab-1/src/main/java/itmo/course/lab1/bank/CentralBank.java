package itmo.course.lab1.bank;

import itmo.course.lab1.account.AbstractAccount;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.EntityNotFoundException;
import itmo.course.lab1.exception.ValidationException;
import itmo.course.lab1.transaction.DepositTransaction;
import itmo.course.lab1.transaction.Transaction;
import itmo.course.lab1.transaction.TransferTransaction;
import itmo.course.lab1.transaction.WithdrawalTransaction;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CentralBank {
    private final Map<UUID, Bank> banks;
    private final Map<UUID, AbstractAccount> accountIndex;
    private final Map<UUID, Transaction> transactions;
    private LocalDate currentDate;

    public CentralBank() {
        this(LocalDate.now());
    }

    public CentralBank(LocalDate currentDate) {
        if (currentDate == null) {
            throw new ValidationException("Текущая дата обязательна");
        }

        this.currentDate = currentDate;
        this.banks = new LinkedHashMap<>();
        this.accountIndex = new LinkedHashMap<>();
        this.transactions = new LinkedHashMap<>();
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public Bank createBank(String name, BankSettings settings) {
        Bank bank = new Bank(name, settings);
        banks.put(bank.getId(), bank);
        return bank;
    }

    public Bank findBank(UUID bankId) {
        Bank bank = banks.get(bankId);
        if (bank == null) {
            throw new EntityNotFoundException("Банк не найден: " + bankId);
        }

        return bank;
    }

    public List<Bank> getBanks() {
        return Collections.unmodifiableList(new ArrayList<>(banks.values()));
    }

    public Client registerClient(UUID bankId, Client client) {
        return findBank(bankId).registerClient(client);
    }

    public AbstractAccount openDebitAccount(UUID bankId, UUID clientId) {
        AbstractAccount account = findBank(bankId).openDebitAccount(clientId);
        accountIndex.put(account.getId(), account);
        return account;
    }

    public AbstractAccount openCreditAccount(UUID bankId, UUID clientId) {
        AbstractAccount account = findBank(bankId).openCreditAccount(clientId);
        accountIndex.put(account.getId(), account);
        return account;
    }

    public AbstractAccount openDepositAccount(UUID bankId, UUID clientId, BigDecimal initialDeposit, Period term) {
        AbstractAccount account = findBank(bankId).openDepositAccount(clientId, initialDeposit, term, currentDate);
        accountIndex.put(account.getId(), account);
        return account;
    }

    public AbstractAccount findAccount(UUID accountId) {
        AbstractAccount account = accountIndex.get(accountId);
        if (account == null) {
            throw new EntityNotFoundException("Счет не найден: " + accountId);
        }

        return account;
    }

    public Transaction deposit(UUID accountId, BigDecimal amount) {
        MoneyUtils.requirePositive(amount, "Сумма пополнения должна быть больше нуля");
        AbstractAccount account = findAccount(accountId);
        Transaction transaction = new DepositTransaction(account, amount);
        transaction.execute(currentDate);
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public Transaction withdraw(UUID accountId, BigDecimal amount) {
        MoneyUtils.requirePositive(amount, "Сумма снятия должна быть больше нуля");
        AbstractAccount account = findAccount(accountId);
        Transaction transaction = new WithdrawalTransaction(account, amount);
        transaction.execute(currentDate);
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public Transaction transfer(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount) {
        if (sourceAccountId.equals(targetAccountId)) {
            throw new ValidationException("Нельзя переводить деньги на тот же счет");
        }

        MoneyUtils.requirePositive(amount, "Сумма перевода должна быть больше нуля");
        AbstractAccount sourceAccount = findAccount(sourceAccountId);
        AbstractAccount targetAccount = findAccount(targetAccountId);
        Transaction transaction = new TransferTransaction(sourceAccount, targetAccount, amount);
        transaction.execute(currentDate);
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public void cancelTransaction(UUID transactionId) {
        Transaction transaction = findTransaction(transactionId);
        transaction.cancel(currentDate);
    }

    public Transaction findTransaction(UUID transactionId) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new EntityNotFoundException("Транзакция не найдена: " + transactionId);
        }

        return transaction;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(new ArrayList<>(transactions.values()));
    }

    public void advanceDays(int days) {
        if (days < 0) {
            throw new ValidationException("Нельзя перематывать время назад");
        }

        for (int index = 0; index < days; index++) {
            LocalDate nextDate = currentDate.plusDays(1);
            for (Bank bank : banks.values()) {
                bank.processDay(currentDate);
            }

            if (nextDate.getMonth() != currentDate.getMonth() || nextDate.getYear() != currentDate.getYear()) {
                for (Bank bank : banks.values()) {
                    bank.processMonthEnd(currentDate);
                }
            }

            currentDate = nextDate;
        }
    }

    public void advanceMonths(int months) {
        if (months < 0) {
            throw new ValidationException("Нельзя перематывать время назад");
        }

        LocalDate targetDate = currentDate.plusMonths(months);
        advanceUntil(targetDate);
    }

    public void advanceYears(int years) {
        if (years < 0) {
            throw new ValidationException("Нельзя перематывать время назад");
        }

        LocalDate targetDate = currentDate.plusYears(years);
        advanceUntil(targetDate);
    }

    private void advanceUntil(LocalDate targetDate) {
        while (currentDate.isBefore(targetDate)) {
            advanceDays(1);
        }
    }
}
