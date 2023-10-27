package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationRepository;
import pro.sky.telegrambot.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public boolean isMessageUnique(long chatId, String notification, LocalDateTime time) {
        Notification existingNotification = notificationRepository.findByChatIdAndReminderTextAndReminderDateTime(chatId, notification, time);
        return existingNotification == null;
    }

    @Override
    public List<Notification> findByTime(LocalDateTime currentDate) {
        return notificationRepository.findByReminderDateTime(currentDate);
    }

    @Override
    public void delete(Notification notification) {
        notificationRepository.delete(notification);
    }
}
