package itmo.course.lab1.bank;

import itmo.course.lab1.exception.ValidationException;
import itmo.course.lab1.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepositRatePolicy {
    private final List<DepositInterestBracket> brackets;

    public DepositRatePolicy(List<DepositInterestBracket> brackets) {
        if (brackets == null || brackets.isEmpty()) {
            throw new ValidationException("Нужна хотя бы одна ставка для депозита");
        }

        this.brackets = new ArrayList<>(brackets);
    }

    public static DepositRatePolicy defaultPolicy() {
        List<DepositInterestBracket> brackets = new ArrayList<>();
        brackets.add(new DepositInterestBracket(new BigDecimal("50000"), new BigDecimal("3.0")));
        brackets.add(new DepositInterestBracket(new BigDecimal("100000"), new BigDecimal("3.5")));
        brackets.add(new DepositInterestBracket(null, new BigDecimal("4.0")));
        return new DepositRatePolicy(brackets);
    }

    public BigDecimal findRate(BigDecimal initialAmount) {
        BigDecimal safeAmount = MoneyUtils.requirePositive(initialAmount, "Начальная сумма должна быть больше нуля");
        for (DepositInterestBracket bracket : brackets) {
            if (bracket.matches(safeAmount)) {
                return bracket.getAnnualRatePercent();
            }
        }

        throw new ValidationException("Не удалось подобрать ставку для депозита");
    }

    public List<DepositInterestBracket> getBrackets() {
        return Collections.unmodifiableList(brackets);
    }
}
