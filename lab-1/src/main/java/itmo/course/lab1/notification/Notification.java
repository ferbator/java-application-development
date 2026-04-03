package itmo.course.lab1.notification;

import java.time.LocalDateTime;

public class Notification {
    private final String bankName;
    private final String message;
    private final LocalDateTime createdAt;

    public Notification(String bankName, String message) {
        this.bankName = bankName;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public String getBankName() {
        return bankName;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
