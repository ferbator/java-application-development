package itmo.course.lab1.notification;

import itmo.course.lab1.client.Client;

public class PushNotificationChannel implements NotificationChannel {
    private final String deviceId;

    public PushNotificationChannel(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public void send(Client client, Notification notification) {
        System.out.println("[PUSH " + deviceId + "][" + notification.getBankName() + "] "
                + client.getFullName() + ": " + notification.getMessage());
    }
}
