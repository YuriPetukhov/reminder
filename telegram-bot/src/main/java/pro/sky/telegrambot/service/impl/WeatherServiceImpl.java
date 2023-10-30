package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pro.sky.telegrambot.model.WeatherNotification;
import pro.sky.telegrambot.model.weather.Weather;
import pro.sky.telegrambot.repository.WeatherNotificationRepository;
import pro.sky.telegrambot.service.WeatherService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J logging
public class WeatherServiceImpl implements WeatherService {

    // Values injected from the properties file
    @Value("${weather-forecast-service.url}")
    private String url;

    @Value("${weather-forecast-service.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final WeatherNotificationRepository weatherNotificationRepository;


    @Override
    public Weather getWeather(String city) {
        // Log a debug message when a weather request is received
        log.info("Received request for weather in city: {}", city);

        // Make a GET request to the weather service and retrieve the response
        Weather weather = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(HttpHeaders.EMPTY),
                Weather.class,
                city,
                apiKey
        ).getBody();

        log.debug("Received weather data for city {}: {}", city, weather);

        return weather;
    }

    @Override
    public boolean isMessageUnique(long chatId, String city, LocalDateTime time) {
        log.info("Checking if the notification already exists for weather in city: {}", city);
        // Check if the notification already exists in the repository
        WeatherNotification existingNotification = weatherNotificationRepository
                .findByChatIdAndCityNameAndReminderDateTime(chatId, city, time);

        // Return true if the notification doesn't exist (is unique), false otherwise
        return existingNotification == null;
    }

    @Override
    public void save(WeatherNotification weatherNotification) {
        // Save the notification to the repository
        log.info("Saving weather notification: {}", weatherNotification);
        weatherNotificationRepository.save(weatherNotification);
    }

    @Override
    public List<WeatherNotification> findByTime(LocalDateTime currentDate) {
        // Find notifications for the current date
        log.info("Finding weather notifications for date: {}", currentDate);
        return weatherNotificationRepository
                .findByReminderDateTime(currentDate);
    }

    @Override
    public void delete(WeatherNotification weatherNotification) {
        // Delete the notification from the repository
        log.info("Deleting weather notification: {}", weatherNotification);
        weatherNotificationRepository.delete(weatherNotification);
    }
}
