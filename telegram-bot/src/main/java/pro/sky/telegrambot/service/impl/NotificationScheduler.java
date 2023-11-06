package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.model.WeatherNotification;
import pro.sky.telegrambot.model.weather.Weather;
import pro.sky.telegrambot.service.NotificationService;
import pro.sky.telegrambot.service.WeatherService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J logging
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final WeatherService weatherService;
    private final TelegramBot telegramBot;

    // Method that is run every minute to check and send notifications
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendDueNotifications() {
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        log.info("Checking for due notifications at: {}", currentDate);

        List<Notification> dueNotifications = notificationService.findByTime(currentDate);

        // Sending due notifications and deleting them from the list
        for (Notification notification : dueNotifications) {
            log.info("Sending due notification for chat ID: {}", notification.getChatId());
            telegramBot.execute(new SendMessage(notification.getChatId(), notification.getReminderText()));
            notificationService.delete(notification);
        }

        List<WeatherNotification> weatherDueNotifications = weatherService.findByTime(currentDate);

        // Sending due weather notifications and deleting them from the list
        for (WeatherNotification notification : weatherDueNotifications) {
            Weather weather = weatherService.getWeather(notification.getCityName());
            SendMessage message = new SendMessage(notification.getChatId(), notification.getCityName() + ": погода: " + weather);
            log.info("Sending due weather notification for city: {}", notification.getCityName());
            telegramBot.execute(message);
            weatherService.delete(notification);
        }
    }

}
