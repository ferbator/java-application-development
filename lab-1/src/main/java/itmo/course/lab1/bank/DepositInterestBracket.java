package itmo.course.lab1.bank;

import itmo.course.lab1.exception.ValidationException;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;

public class DepositInterestBracket {
    private final BigDecimal upperBound;
    private final BigDecimal annualRatePercent;

    public DepositInterestBracket(BigDecimal upperBound, BigDecimal annualRatePercent) {
        if (upperBound != null && upperBound.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Верхняя граница должна быть положительной");
        }

        this.upperBound = upperBound == null ? null : MoneyUtils.normalize(upperBound);
        this.annualRatePercent = MoneyUtils.requireNonNegative(annualRatePercent, "Ставка не может быть отрицательной");
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public BigDecimal getAnnualRatePercent() {
        return annualRatePercent;
    }

    public boolean matches(BigDecimal initialAmount) {
        if (upperBound == null) {
            return true;
        }

        return MoneyUtils.normalize(initialAmount).compareTo(upperBound) <= 0;
    }
}
