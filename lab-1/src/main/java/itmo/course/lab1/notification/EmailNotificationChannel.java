package itmo.course.lab1.notification;

import itmo.course.lab1.client.Client;

public class EmailNotificationChannel implements NotificationChannel {
    private final String email;

    public EmailNotificationChannel(String email) {
        this.email = email;
    }

    @Override
    public void send(Client client, Notification notification) {
        System.out.println("[EMAIL " + email + "][" + notification.getBankName() + "] "
                + client.getFullName() + ": " + notification.getMessage());
    }
}
