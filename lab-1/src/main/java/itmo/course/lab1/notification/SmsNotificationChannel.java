package itmo.course.lab1.notification;

import itmo.course.lab1.client.Client;

public class SmsNotificationChannel implements NotificationChannel {
    private final String phoneNumber;

    public SmsNotificationChannel(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void send(Client client, Notification notification) {
        System.out.println("[SMS " + phoneNumber + "][" + notification.getBankName() + "] "
                + client.getFullName() + ": " + notification.getMessage());
    }
}
