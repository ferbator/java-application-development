package itmo.course.lab1.exception;

public class BankingException extends RuntimeException {
    public BankingException(String message) {
        super(message);
    }
}
