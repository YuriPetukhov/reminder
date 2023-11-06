package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.NotificationService;
import pro.sky.telegrambot.service.WeatherService;

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
        notificationService.sendDueNotifications();
        weatherService.sendDueWeatherNotifications();
    }

}
