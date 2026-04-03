package itmo.course.lab1.transaction;

import itmo.course.lab1.exception.TransactionException;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public abstract class AbstractTransaction implements Transaction {
    private final UUID id;
    private final BigDecimal amount;
    private boolean executed;
    private boolean cancelled;

    protected AbstractTransaction(BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.amount = MoneyUtils.requirePositive(amount, "Сумма транзакции должна быть больше нуля");
        this.executed = false;
        this.cancelled = false;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void execute(LocalDate currentDate) {
        if (executed) {
            throw new TransactionException("Транзакция уже выполнена");
        }

        doExecute(currentDate);
        executed = true;
    }

    @Override
    public void cancel(LocalDate currentDate) {
        if (!executed) {
            throw new TransactionException("Нельзя отменить невыполненную транзакцию");
        }
        if (cancelled) {
            throw new TransactionException("Транзакцию нельзя отменить дважды");
        }

        doCancel(currentDate);
        cancelled = true;
    }

    protected abstract void doExecute(LocalDate currentDate);

    protected abstract void doCancel(LocalDate currentDate);
}
