package itmo.course.lab1.util;

import itmo.course.lab1.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {
    public static final BigDecimal HUNDRED = new BigDecimal("100");
    public static final BigDecimal DAYS_IN_YEAR = new BigDecimal("365");

    private MoneyUtils() {
    }

    public static BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            throw new ValidationException("Сумма не может быть null");
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal normalizeInternal(BigDecimal amount) {
        if (amount == null) {
            throw new ValidationException("Сумма не может быть null");
        }

        return amount.setScale(10, RoundingMode.HALF_UP);
    }

    public static BigDecimal requirePositive(BigDecimal amount, String message) {
        BigDecimal normalizedAmount = normalize(amount);
        if (normalizedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(message);
        }

        return normalizedAmount;
    }

    public static BigDecimal requireNonNegative(BigDecimal amount, String message) {
        BigDecimal normalizedAmount = normalize(amount);
        if (normalizedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(message);
        }

        return normalizedAmount;
    }

    public static BigDecimal calculateDailyInterest(BigDecimal balance, BigDecimal annualRatePercent) {
        BigDecimal safeBalance = normalize(balance);
        BigDecimal safeRate = requireNonNegative(annualRatePercent, "Ставка не может быть отрицательной");

        return safeBalance
                .multiply(safeRate)
                .divide(HUNDRED, 10, RoundingMode.HALF_UP)
                .divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP);
    }

    public static String format(BigDecimal amount) {
        return normalize(amount).toPlainString();
    }
}
