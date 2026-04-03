package itmo.course.lab1.exception;

public class TransactionException extends BankingException {
    public TransactionException(String message) {
        super(message);
    }
}
