package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationRepository;
import pro.sky.telegrambot.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J logging
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    // Adding new notification
    @Override
    public void save(Notification notification) {
        log.info("Saving notification {}", notification);
        notificationRepository.save(notification);
    }

    // Check if message is unique by chatId, notification text and reminder date
    @Override
    public boolean isMessageUnique(long chatId, String notification, LocalDateTime time) {
        Notification existingNotification = notificationRepository.findByChatIdAndReminderTextAndReminderDateTime(chatId, notification, time);
        boolean isUnique = existingNotification == null;
        log.info("Checking uniqueness for notification with chatId {} and text {}. Is unique: {}", chatId, notification, isUnique);
        return isUnique;
    }

    // Returning a list of notifications for specific time
    @Override
    public List<Notification> findByTime(LocalDateTime currentDate) {
        log.info("Finding notifications with date {}", currentDate);
        return notificationRepository.findByReminderDateTime(currentDate);
    }

    // Deleting a specific notification
    @Override
    public void delete(Notification notification) {
        log.info("Deleting notification {}", notification);
        notificationRepository.delete(notification);
    }
}
