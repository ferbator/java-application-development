package itmo.course.lab1.exception;

public class AccountOperationException extends BankingException {
    public AccountOperationException(String message) {
        super(message);
    }
}
