package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.WeatherNotification;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherNotificationRepository extends JpaRepository<WeatherNotification, Long> {
    WeatherNotification findByChatIdAndCityNameAndReminderDateTime(long chatId, String cityName, LocalDateTime reminderDateTime);

    List<WeatherNotification> findByReminderDateTime(LocalDateTime currentDate);
}
