package pro.sky.telegrambot.service;


import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.model.WeatherNotification;
import pro.sky.telegrambot.model.weather.Weather;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherService {
    Weather getWeather(String city);

    boolean isMessageUnique(long chatId, String city, LocalDateTime time);

    void save(WeatherNotification weatherNotification);

    List<WeatherNotification> findByTime(LocalDateTime currentDate);

    void delete(WeatherNotification weatherNotification);
}
