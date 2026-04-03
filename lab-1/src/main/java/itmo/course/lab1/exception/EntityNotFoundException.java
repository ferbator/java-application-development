package itmo.course.lab1.exception;

public class EntityNotFoundException extends BankingException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
