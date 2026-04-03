package itmo.course.lab1;

import itmo.course.lab1.account.AbstractAccount;
import itmo.course.lab1.account.DepositAccount;
import itmo.course.lab1.bank.Bank;
import itmo.course.lab1.bank.BankSettings;
import itmo.course.lab1.bank.CentralBank;
import itmo.course.lab1.client.Client;
import itmo.course.lab1.exception.AccountOperationException;
import itmo.course.lab1.exception.TransactionException;
import itmo.course.lab1.notification.Notification;
import itmo.course.lab1.notification.NotificationChannel;
import itmo.course.lab1.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankingSystemTest {
    @Test
    void suspiciousClientCannotWithdrawAboveLimitUntilDataIsCompleted() {
        CentralBank centralBank = new CentralBank(LocalDate.of(2026, 1, 1));
        Bank bank = centralBank.createBank("Test Bank", BankSettings.defaultSettings());

        Client client = Client.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .build();

        centralBank.registerClient(bank.getId(), client);
        AbstractAccount account = centralBank.openDebitAccount(bank.getId(), client.getId());
        centralBank.deposit(account.getId(), new BigDecimal("1500"));

        assertTrue(client.isSuspicious());
        assertThrows(
                AccountOperationException.class,
                () -> centralBank.withdraw(account.getId(), new BigDecimal("1200"))
        );

        client.updateAddress("Saint Petersburg");
        client.updatePassportNumber("4000 123456");

        centralBank.withdraw(account.getId(), new BigDecimal("1200"));

        assertFalse(client.isSuspicious());
        assertMoneyEquals("300.00", account.getBalance());
    }

    @Test
    void debitAccountAccruesInterestAndPaysItAtMonthEnd() {
        CentralBank centralBank = new CentralBank(LocalDate.of(2026, 1, 1));
        Bank bank = centralBank.createBank("Debit Bank", BankSettings.defaultSettings());
        Client client = createVerifiedClient();

        centralBank.registerClient(bank.getId(), client);
        AbstractAccount account = centralBank.openDebitAccount(bank.getId(), client.getId());
        centralBank.deposit(account.getId(), new BigDecimal("100000"));

        centralBank.advanceMonths(1);

        assertMoneyEquals("100310.00", account.getBalance());
    }

    @Test
    void depositCannotBeWithdrawnBeforeMaturityDate() {
        CentralBank centralBank = new CentralBank(LocalDate.of(2026, 1, 1));
        Bank bank = centralBank.createBank("Deposit Bank", BankSettings.defaultSettings());
        Client client = createVerifiedClient();

        centralBank.registerClient(bank.getId(), client);
        AbstractAccount account = centralBank.openDepositAccount(
                bank.getId(),
                client.getId(),
                new BigDecimal("60000"),
                Period.ofDays(10)
        );

        DepositAccount depositAccount = (DepositAccount) account;

        assertMoneyEquals("60000.00", depositAccount.getBalance());
        assertMoneyEquals("3.50", depositAccount.getFixedAnnualInterestRate());
        assertThrows(
                AccountOperationException.class,
                () -> centralBank.withdraw(depositAccount.getId(), new BigDecimal("100"))
        );

        centralBank.advanceDays(10);
        centralBank.withdraw(depositAccount.getId(), new BigDecimal("100"));

        assertMoneyEquals("59900.00", depositAccount.getBalance());
    }

    @Test
    void creditAccountChargesMonthlyCommissionWhenBalanceIsNegative() {
        CentralBank centralBank = new CentralBank(LocalDate.of(2026, 1, 1));
        Bank bank = centralBank.createBank("Credit Bank", BankSettings.defaultSettings());
        Client client = createVerifiedClient();

        centralBank.registerClient(bank.getId(), client);
        AbstractAccount account = centralBank.openCreditAccount(bank.getId(), client.getId());
        centralBank.withdraw(account.getId(), new BigDecimal("2000"));

        centralBank.advanceMonths(1);

        assertMoneyEquals("-2500.00", account.getBalance());
    }

    @Test
    void transferBetweenBanksCanBeCancelled() {
        CentralBank centralBank = new CentralBank(LocalDate.of(2026, 1, 1));
        Bank firstBank = centralBank.createBank("First Bank", BankSettings.defaultSettings());
        Bank secondBank = centralBank.createBank("Second Bank", BankSettings.defaultSettings());
        Client firstClient = createVerifiedClient();
        Client secondClient = createSecondVerifiedClient();

        centralBank.registerClient(firstBank.getId(), firstClient);
        centralBank.registerClient(secondBank.getId(), secondClient);

        AbstractAccount sourceAccount = centralBank.openDebitAccount(firstBank.getId(), firstClient.getId());
        AbstractAccount targetAccount = centralBank.openDebitAccount(secondBank.getId(), secondClient.getId());

        centralBank.deposit(sourceAccount.getId(), new BigDecimal("5000"));
        centralBank.deposit(targetAccount.getId(), new BigDecimal("1000"));

        Transaction transaction = centralBank.transfer(
                sourceAccount.getId(),
                targetAccount.getId(),
                new BigDecimal("700")
        );

        assertMoneyEquals("4300.00", sourceAccount.getBalance());
        assertMoneyEquals("1700.00", targetAccount.getBalance());

        centralBank.cancelTransaction(transaction.getId());

        assertMoneyEquals("5000.00", sourceAccount.getBalance());
        assertMoneyEquals("1000.00", targetAccount.getBalance());
        assertThrows(TransactionException.class, () -> centralBank.cancelTransaction(transaction.getId()));
    }

    @Test
    void subscribersReceiveNotificationWhenBankSettingsChange() {
        CentralBank centralBank = new CentralBank(LocalDate.of(2026, 1, 1));
        Bank bank = centralBank.createBank("Notify Bank", BankSettings.defaultSettings());
        Client client = createVerifiedClient();
        TestNotificationChannel channel = new TestNotificationChannel();

        centralBank.registerClient(bank.getId(), client);
        bank.subscribe(client, channel);

        bank.updateCreditLimit(new BigDecimal("15000"));

        assertEquals(1, channel.messages.size());
        assertTrue(channel.messages.getFirst().contains("Изменился кредитный лимит"));
    }

    private Client createVerifiedClient() {
        return Client.builder()
                .firstName("Anna")
                .lastName("Smirnova")
                .address("Moscow")
                .passportNumber("4000 111111")
                .build();
    }

    private Client createSecondVerifiedClient() {
        return Client.builder()
                .firstName("Petr")
                .lastName("Ivanov")
                .address("Kazan")
                .passportNumber("4000 222222")
                .build();
    }

    private void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0, actual.compareTo(new BigDecimal(expected)));
    }

    private static class TestNotificationChannel implements NotificationChannel {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void send(Client client, Notification notification) {
            messages.add(notification.getMessage());
        }
    }
}
