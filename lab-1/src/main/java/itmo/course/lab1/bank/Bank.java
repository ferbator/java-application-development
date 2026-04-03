package itmo.course.lab1.bank;

import itmo.course.lab1.account.AbstractAccount;
import itmo.course.lab1.account.AccountFactory;
import itmo.course.lab1.account.CreditAccount;
import itmo.course.lab1.account.DebitAccount;
import itmo.course.lab1.account.DefaultAccountFactory;
import itmo.course.lab1.account.DepositAccount;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.EntityNotFoundException;
import itmo.course.lab1.exception.ValidationException;
import itmo.course.lab1.notification.Notification;
import itmo.course.lab1.notification.NotificationChannel;
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

public class Bank {
    private final UUID id;
    private final String name;
    private final Map<UUID, Client> clients;
    private final Map<UUID, AbstractAccount> accounts;
    private final Map<UUID, List<NotificationChannel>> subscribers;
    private final AccountFactory accountFactory;
    private final BankSettings settings;

    public Bank(String name, BankSettings settings) {
        this(name, settings, new DefaultAccountFactory());
    }

    public Bank(String name, BankSettings settings, AccountFactory accountFactory) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Название банка обязательно");
        }
        if (settings == null) {
            throw new ValidationException("Настройки банка обязательны");
        }
        if (accountFactory == null) {
            throw new ValidationException("Фабрика счетов обязательна");
        }

        this.id = UUID.randomUUID();
        this.name = name.trim();
        this.settings = settings;
        this.accountFactory = accountFactory;
        this.clients = new LinkedHashMap<>();
        this.accounts = new LinkedHashMap<>();
        this.subscribers = new LinkedHashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BankSettings getSettings() {
        return settings;
    }

    public Client registerClient(Client client) {
        if (client == null) {
            throw new ValidationException("Клиент обязателен");
        }

        clients.put(client.getId(), client);
        return client;
    }

    public Client findClient(UUID clientId) {
        Client client = clients.get(clientId);
        if (client == null) {
            throw new EntityNotFoundException("Клиент не найден: " + clientId);
        }

        return client;
    }

    public DebitAccount openDebitAccount(UUID clientId) {
        Client client = findClient(clientId);
        DebitAccount account = accountFactory.createDebitAccount(this, client);
        accounts.put(account.getId(), account);
        return account;
    }

    public CreditAccount openCreditAccount(UUID clientId) {
        Client client = findClient(clientId);
        CreditAccount account = accountFactory.createCreditAccount(this, client);
        accounts.put(account.getId(), account);
        return account;
    }

    public DepositAccount openDepositAccount(UUID clientId, BigDecimal initialDeposit, Period term, LocalDate openedAt) {
        Client client = findClient(clientId);
        DepositAccount account = accountFactory.createDepositAccount(this, client, initialDeposit, openedAt, term);
        account.deposit(initialDeposit);
        accounts.put(account.getId(), account);
        return account;
    }

    public AbstractAccount findAccount(UUID accountId) {
        AbstractAccount account = accounts.get(accountId);
        if (account == null) {
            throw new EntityNotFoundException("Счет не найден: " + accountId);
        }

        return account;
    }

    public void subscribe(Client client, NotificationChannel channel) {
        if (!clients.containsKey(client.getId())) {
            throw new EntityNotFoundException("Нельзя подписать клиента, которого нет в банке");
        }
        if (channel == null) {
            throw new ValidationException("Канал уведомлений обязателен");
        }

        List<NotificationChannel> channels = subscribers.computeIfAbsent(client.getId(), ignored -> new ArrayList<>());
        channels.add(channel);
    }

    public void updateDebitInterestRate(BigDecimal newRate) {
        settings.setDebitAnnualInterestRate(newRate);
        notifySubscribers("Изменился процент по дебетовому счету: " + MoneyUtils.format(newRate) + "%");
    }

    public void updateSuspiciousClientLimit(BigDecimal newLimit) {
        settings.setSuspiciousClientLimit(newLimit);
        notifySubscribers("Изменился лимит для сомнительных клиентов: " + MoneyUtils.format(newLimit));
    }

    public void updateCreditLimit(BigDecimal newCreditLimit) {
        settings.setCreditLimit(newCreditLimit);
        notifySubscribers("Изменился кредитный лимит: " + MoneyUtils.format(newCreditLimit));
    }

    public void updateCreditNegativeBalanceCommission(BigDecimal newCommission) {
        settings.setCreditNegativeBalanceCommission(newCommission);
        notifySubscribers("Изменилась комиссия за отрицательный баланс: " + MoneyUtils.format(newCommission));
    }

    public void updateDepositRatePolicy(DepositRatePolicy newPolicy) {
        settings.setDepositRatePolicy(newPolicy);
        notifySubscribers("Изменилась политика ставок по депозитам");
    }

    public void processDay(LocalDate currentDate) {
        for (AbstractAccount account : accounts.values()) {
            account.onDayPassed(currentDate);
        }
    }

    public void processMonthEnd(LocalDate currentDate) {
        for (AbstractAccount account : accounts.values()) {
            account.onMonthEnd(currentDate);
        }
    }

    public List<Client> getClients() {
        return Collections.unmodifiableList(new ArrayList<>(clients.values()));
    }

    public List<AbstractAccount> getAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts.values()));
    }

    private void notifySubscribers(String message) {
        Notification notification = new Notification(name, message);
        for (Map.Entry<UUID, List<NotificationChannel>> entry : subscribers.entrySet()) {
            Client client = clients.get(entry.getKey());
            if (client == null) {
                continue;
            }

            for (NotificationChannel channel : entry.getValue()) {
                channel.send(client, notification);
            }
        }
    }
}
