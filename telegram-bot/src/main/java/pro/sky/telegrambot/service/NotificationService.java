package pro.sky.telegrambot.service;

import pro.sky.telegrambot.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    void save(Notification notification);
    boolean isMessageUnique(long chatId, String notification, LocalDateTime time);

    List<Notification> findByTime(LocalDateTime currentDate);

    void delete(Notification notification);

    // Method that is run to check and send notifications
    void sendDueNotifications();
}
