package itmo.course.lab1.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface Transaction {
    UUID getId();

    BigDecimal getAmount();

    String getDescription();

    boolean isExecuted();

    boolean isCancelled();

    void execute(LocalDate currentDate);

    void cancel(LocalDate currentDate);
}
