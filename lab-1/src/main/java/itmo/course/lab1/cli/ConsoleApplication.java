package itmo.course.lab1.cli;

import itmo.course.lab1.account.AbstractAccount;
import itmo.course.lab1.account.DepositAccount;
import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.bank.BankSettings;
import itmo.course.lab1.bank.CentralBank;
import itmo.course.lab1.bank.DepositInterestBracket;
import itmo.course.lab1.bank.DepositRatePolicy;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.client.ClientBuilder;
import itmo.course.lab1.exception.BankingException;
import itmo.course.lab1.notification.ConsoleNotificationChannel;
import itmo.course.lab1.notification.EmailNotificationChannel;
import itmo.course.lab1.notification.NotificationChannel;
import itmo.course.lab1.notification.PushNotificationChannel;
import itmo.course.lab1.notification.SmsNotificationChannel;
import itmo.course.lab1.transaction.Transaction;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleApplication {
    private final Scanner scanner;
    private final CentralBank centralBank;

    public ConsoleApplication() {
        this.scanner = new Scanner(System.in);
        this.centralBank = new CentralBank();
    }

    public void run() {
        boolean running = true;

        System.out.println("Лабораторная 1. Банковская система");
        System.out.println("Текущая дата системы: " + centralBank.getCurrentDate());

        while (running) {
            printMenu();
            String command = readLine("Выберите пункт");

            try {
                switch (command) {
                    case "1":
                        createBank();
                        break;
                    case "2":
                        showBanks();
                        break;
                    case "3":
                        createClient();
                        break;
                    case "4":
                        updateClient();
                        break;
                    case "5":
                        subscribeClient();
                        break;
                    case "6":
                        openDebitAccount();
                        break;
                    case "7":
                        openDepositAccount();
                        break;
                    case "8":
                        openCreditAccount();
                        break;
                    case "9":
                        showBankClients();
                        break;
                    case "10":
                        showBankAccounts();
                        break;
                    case "11":
                        depositMoney();
                        break;
                    case "12":
                        withdrawMoney();
                        break;
                    case "13":
                        transferMoney();
                        break;
                    case "14":
                        cancelTransaction();
                        break;
                    case "15":
                        showTransactions();
                        break;
                    case "16":
                        updateBankSettings();
                        break;
                    case "17":
                        advanceDays();
                        break;
                    case "18":
                        advanceMonths();
                        break;
                    case "19":
                        advanceYears();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Работа завершена.");
                        break;
                    default:
                        System.out.println("Неизвестная команда.");
                }
            } catch (BankingException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            } catch (IllegalArgumentException exception) {
                System.out.println("Ошибка ввода: " + exception.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Создайте банк");
        System.out.println("2. Покажите банки");
        System.out.println("3. Создайте клиента");
        System.out.println("4. Обновите адрес или паспорт клиента");
        System.out.println("5. Подпишите клиента на уведомления");
        System.out.println("6. Откройте дебетовый счёт");
        System.out.println("7. Откройте депозит");
        System.out.println("8. Откройте кредитный счёт");
        System.out.println("9. Покажите клиентов банка");
        System.out.println("10. Покажите счёта банка");
        System.out.println("11. Пополните счёт");
        System.out.println("12. Снимите деньги");
        System.out.println("13. Сделайте перевод");
        System.out.println("14. Отмените транзакцию");
        System.out.println("15. Покажите транзакции");
        System.out.println("16. Измените настройки банка");
        System.out.println("17. Перемотайте время на дни");
        System.out.println("18. Перемотайте время на месяцы");
        System.out.println("19. Перемотайте время на годы");
        System.out.println("0. Выход");
        System.out.println("Дата системы: " + centralBank.getCurrentDate());
    }

    private void createBank() {
        String name = readLine("Введите название банка");
        String useDefault = readLine("Использовать настройки по умолчанию? (да/нет)");

        BankSettings settings;
        if (isYesAnswer(useDefault)) {
            settings = BankSettings.defaultSettings();
        } else {
            settings = createCustomSettings();
        }

        Bank bank = centralBank.createBank(name, settings);
        System.out.println("Банк создан. ID: " + bank.getId());
    }

    private BankSettings createCustomSettings() {
        BigDecimal debitRate = readBigDecimal("Введите процент по дебетовому счёту, например 3.65");
        BigDecimal suspiciousLimit = readBigDecimal("Введите лимит для сомнительного клиента");
        BigDecimal creditLimit = readBigDecimal("Введите кредитный лимит");
        BigDecimal creditCommission = readBigDecimal("Введите комиссию при отрицательном балансе");
        DepositRatePolicy depositRatePolicy = readDepositPolicy();

        return new BankSettings(debitRate, suspiciousLimit, creditLimit, creditCommission, depositRatePolicy);
    }

    private DepositRatePolicy readDepositPolicy() {
        List<DepositInterestBracket> brackets = new ArrayList<>();
        BigDecimal firstRate = readBigDecimal("Введите ставку по депозиту до 50000");
        BigDecimal secondRate = readBigDecimal("Введите ставку по депозиту до 100000");
        BigDecimal thirdRate = readBigDecimal("Введите ставку по депозиту больше 100000");

        brackets.add(new DepositInterestBracket(new BigDecimal("50000"), firstRate));
        brackets.add(new DepositInterestBracket(new BigDecimal("100000"), secondRate));
        brackets.add(new DepositInterestBracket(null, thirdRate));
        return new DepositRatePolicy(brackets);
    }

    private void showBanks() {
        List<Bank> banks = centralBank.getBanks();
        if (banks.isEmpty()) {
            System.out.println("Банков пока нет");
            return;
        }

        for (Bank bank : banks) {
            System.out.println("Банк: " + bank.getName());
            System.out.println("  ID: " + bank.getId());
            System.out.println("  Клиентов: " + bank.getClients().size());
            System.out.println("  Счётов: " + bank.getAccounts().size());
            System.out.println("  Дебетовый процент: " + MoneyUtils.format(bank.getSettings().getDebitAnnualInterestRate()) + "%");
            System.out.println("  Лимит сомнительного клиента: " + MoneyUtils.format(bank.getSettings().getSuspiciousClientLimit()));
            System.out.println("  Кредитный лимит: " + MoneyUtils.format(bank.getSettings().getCreditLimit()));
            System.out.println("  Комиссия при минусе: " + MoneyUtils.format(bank.getSettings().getCreditNegativeBalanceCommission()));
        }
    }

    private void createClient() {
        UUID bankId = readUuid("Введите ID банка");
        String firstName = readLine("Введите имя");
        String lastName = readLine("Введите фамилию");
        String address = readOptionalLine("Введите адрес, если он есть");
        String passportNumber = readOptionalLine("Введите паспорт, если он есть");

        ClientBuilder.OptionalDataStep builder = Client.builder()
                .firstName(firstName)
                .lastName(lastName);

        if (address != null) {
            builder.address(address);
        }

        if (passportNumber != null) {
            builder.passportNumber(passportNumber);
        }

        Client client = builder.build();
        centralBank.registerClient(bankId, client);
        System.out.println("Клиент создан. ID: " + client.getId());
        System.out.println("Сомнительный клиент: " + client.isSuspicious());
    }

    private void updateClient() {
        Bank bank = centralBank.findBank(readUuid("Введите ID банка"));
        Client client = bank.findClient(readUuid("Введите ID клиента"));
        String updateChoice = readLine("Что Вы хотите обновить? address/passport/both");

        if ("address".equalsIgnoreCase(updateChoice) || "both".equalsIgnoreCase(updateChoice)) {
            client.updateAddress(readLine("Введите новый адрес"));
        }

        if ("passport".equalsIgnoreCase(updateChoice) || "both".equalsIgnoreCase(updateChoice)) {
            client.updatePassportNumber(readLine("Введите новый паспорт"));
        }

        System.out.println("Клиент обновлён. Сомнительный клиент: " + client.isSuspicious());
    }

    private void subscribeClient() {
        Bank bank = centralBank.findBank(readUuid("Введите ID банка"));
        Client client = bank.findClient(readUuid("Введите ID клиента"));
        String channelName = readLine("Выберите канал уведомлений: console/email/sms/push");
        NotificationChannel channel = createChannel(channelName);
        bank.subscribe(client, channel);
        System.out.println("Подписка оформлена.");
    }

    private NotificationChannel createChannel(String channelName) {
        if ("console".equalsIgnoreCase(channelName)) {
            return new ConsoleNotificationChannel();
        }
        if ("email".equalsIgnoreCase(channelName)) {
            return new EmailNotificationChannel(readLine("Введите email"));
        }
        if ("sms".equalsIgnoreCase(channelName)) {
            return new SmsNotificationChannel(readLine("Введите телефон"));
        }
        if ("push".equalsIgnoreCase(channelName)) {
            return new PushNotificationChannel(readLine("Введите ID устройства"));
        }

        throw new IllegalArgumentException("Неизвестный канал уведомлений");
    }

    private void openDebitAccount() {
        UUID bankId = readUuid("Введите ID банка");
        UUID clientId = readUuid("Введите ID клиента");
        AbstractAccount account = centralBank.openDebitAccount(bankId, clientId);
        System.out.println("Дебетовый счёт открыт. ID: " + account.getId());
    }

    private void openDepositAccount() {
        UUID bankId = readUuid("Введите ID банка");
        UUID clientId = readUuid("Введите ID клиента");
        BigDecimal initialDeposit = readBigDecimal("Введите начальную сумму депозита");
        int days = readInt("Введите срок депозита в днях");
        AbstractAccount account = centralBank.openDepositAccount(bankId, clientId, initialDeposit, Period.ofDays(days));
        System.out.println("Депозит открыт. ID: " + account.getId());
        if (account instanceof DepositAccount depositAccount) {
            System.out.println("Дата окончания: " + depositAccount.getMaturityDate());
            System.out.println("Ставка: " + MoneyUtils.format(depositAccount.getFixedAnnualInterestRate()) + "%");
        }
    }

    private void openCreditAccount() {
        UUID bankId = readUuid("Введите ID банка");
        UUID clientId = readUuid("Введите ID клиента");
        AbstractAccount account = centralBank.openCreditAccount(bankId, clientId);
        System.out.println("Кредитный счёт открыт. ID: " + account.getId());
    }

    private void showBankAccounts() {
        Bank bank = centralBank.findBank(readUuid("Введите ID банка"));
        if (bank.getAccounts().isEmpty()) {
            System.out.println("У банка пока нет счетов");
            return;
        }

        for (AbstractAccount account : bank.getAccounts()) {
            Client client = account.getOwner();
            System.out.println(account.getType() + " | " + account.getId());
            System.out.println("  Владелец: " + client.getFullName() + " (" + client.getId() + ")");
            System.out.println("  Баланс: " + MoneyUtils.format(account.getBalance()));
            System.out.println("  Сомнительный клиент: " + client.isSuspicious());
            if (account instanceof DepositAccount depositAccount) {
                System.out.println("  Окончание депозита: " + depositAccount.getMaturityDate());
                System.out.println("  Фиксированная ставка: " + MoneyUtils.format(depositAccount.getFixedAnnualInterestRate()) + "%");
            }
        }
    }

    private void showBankClients() {
        Bank bank = centralBank.findBank(readUuid("Введите ID банка"));
        if (bank.getClients().isEmpty()) {
            System.out.println("У банка пока нет клиентов");
            return;
        }

        for (Client client : bank.getClients()) {
            System.out.println(client.getFullName() + " | " + client.getId());
            System.out.println("  Адрес: " + valueOrDash(client.getAddress()));
            System.out.println("  Паспорт: " + valueOrDash(client.getPassportNumber()));
            System.out.println("  Сомнительный: " + client.isSuspicious());
        }
    }

    private void depositMoney() {
        UUID accountId = readUuid("Введите ID счёта");
        BigDecimal amount = readBigDecimal("Введите сумму пополнения");
        Transaction transaction = centralBank.deposit(accountId, amount);
        System.out.println("Пополнение выполнено. Транзакция: " + transaction.getId());
    }

    private void withdrawMoney() {
        UUID accountId = readUuid("Введите ID счёта");
        BigDecimal amount = readBigDecimal("Введите сумму снятия");
        Transaction transaction = centralBank.withdraw(accountId, amount);
        System.out.println("Снятие выполнено. Транзакция: " + transaction.getId());
    }

    private void transferMoney() {
        UUID sourceAccountId = readUuid("Введите ID счёта отправителя");
        UUID targetAccountId = readUuid("Введите ID счёта получателя");
        BigDecimal amount = readBigDecimal("Введите сумму перевода");
        Transaction transaction = centralBank.transfer(sourceAccountId, targetAccountId, amount);
        System.out.println("Перевод выполнен. Транзакция: " + transaction.getId());
    }

    private void cancelTransaction() {
        UUID transactionId = readUuid("Введите ID транзакции");
        centralBank.cancelTransaction(transactionId);
        System.out.println("Транзакция отменена");
    }

    private void showTransactions() {
        List<Transaction> transactions = centralBank.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("Транзакций пока нет");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println("ID: " + transaction.getId());
            System.out.println("  Описание: " + transaction.getDescription());
            System.out.println("  Сумма: " + MoneyUtils.format(transaction.getAmount()));
            System.out.println("  Выполнена: " + transaction.isExecuted());
            System.out.println("  Отменена: " + transaction.isCancelled());
        }
    }

    private void updateBankSettings() {
        Bank bank = centralBank.findBank(readUuid("Введите ID банка"));
        System.out.println("Что Вы хотите изменить?");
        System.out.println("1. Дебетовый процент");
        System.out.println("2. Лимит сомнительного клиента");
        System.out.println("3. Кредитный лимит");
        System.out.println("4. Комиссию по кредиту");
        System.out.println("5. Ставки по депозиту");

        String choice = readLine("Введите номер пункта");
        switch (choice) {
            case "1":
                bank.updateDebitInterestRate(readBigDecimal("Введите новую ставку"));
                break;
            case "2":
                bank.updateSuspiciousClientLimit(readBigDecimal("Введите новый лимит"));
                break;
            case "3":
                bank.updateCreditLimit(readBigDecimal("Введите новый кредитный лимит"));
                break;
            case "4":
                bank.updateCreditNegativeBalanceCommission(readBigDecimal("Введите новую комиссию"));
                break;
            case "5":
                bank.updateDepositRatePolicy(readDepositPolicy());
                break;
            default:
                throw new IllegalArgumentException("Неизвестный пункт");
        }

        System.out.println("Настройки обновлены");
    }

    private void advanceDays() {
        int days = readInt("На сколько дней Вы хотите перемотать время");
        centralBank.advanceDays(days);
        System.out.println("Новая дата: " + centralBank.getCurrentDate());
    }

    private void advanceMonths() {
        int months = readInt("На сколько месяцев Вы хотите перемотать время");
        centralBank.advanceMonths(months);
        System.out.println("Новая дата: " + centralBank.getCurrentDate());
    }

    private void advanceYears() {
        int years = readInt("На сколько лет Вы хотите перемотать время");
        centralBank.advanceYears(years);
        System.out.println("Новая дата: " + centralBank.getCurrentDate());
    }

    private String readLine(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    private String readOptionalLine(String prompt) {
        System.out.print(prompt + ": ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            return null;
        }

        return line;
    }

    private BigDecimal readBigDecimal(String prompt) {
        return new BigDecimal(readLine(prompt));
    }

    private int readInt(String prompt) {
        return Integer.parseInt(readLine(prompt));
    }

    private UUID readUuid(String prompt) {
        return UUID.fromString(readLine(prompt));
    }

    private boolean isYesAnswer(String value) {
        return "да".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
    }

    private String valueOrDash(String value) {
        if (value == null) {
            return "-";
        }

        return value;
    }
}
