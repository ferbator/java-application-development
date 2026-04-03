package itmo.course.lab1.notification;

import itmo.course.lab1.client.Client;

public class ConsoleNotificationChannel implements NotificationChannel {
    @Override
    public void send(Client client, Notification notification) {
        System.out.println("[CONSOLE][" + notification.getBankName() + "] "
                + client.getFullName() + ": " + notification.getMessage());
    }
}
