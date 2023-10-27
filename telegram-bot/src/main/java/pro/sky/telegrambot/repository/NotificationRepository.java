package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReminderDateTime(LocalDateTime date);

    Notification findByChatIdAndReminderTextAndReminderDateTime(long chatId, String reminderText, LocalDateTime reminderDateTime);
}
