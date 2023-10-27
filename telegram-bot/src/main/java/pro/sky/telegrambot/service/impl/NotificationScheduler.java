package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
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
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final WeatherService weatherService;
    private final TelegramBot telegramBot;

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendDueNotifications() {
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<Notification> dueNotifications = notificationService.findByTime(currentDate);
        for (Notification notification : dueNotifications) {
            telegramBot.execute(new SendMessage(notification.getChatId(), notification.getReminderText()));
            notificationService.delete(notification);
        }

        List<WeatherNotification> weatherDueNotifications = weatherService.findByTime(currentDate);
        for (WeatherNotification notification : weatherDueNotifications) {
            Weather weather = weatherService.getWeather(notification.getCityName());
            SendMessage message = new SendMessage(notification.getChatId(), notification.getCityName() + ": погода: " + weather);
            telegramBot.execute(message);
            weatherService.delete(notification);
        }
    }

}
