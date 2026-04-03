package itmo.course.lab1.notification;

import itmo.course.lab1.client.Client;

public interface NotificationChannel {
    void send(Client client, Notification notification);
}
