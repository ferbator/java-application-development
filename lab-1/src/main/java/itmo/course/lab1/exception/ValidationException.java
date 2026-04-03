package itmo.course.lab1.exception;

public class ValidationException extends BankingException {
    public ValidationException(String message) {
        super(message);
    }
}
