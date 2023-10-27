package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather-forecast-service.url}")
    private String url;

    @Value("${weather-forecast-service.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final WeatherNotificationRepository weatherNotificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

    public Weather getWeather(String city) {
        logger.debug("weather request is received for city: {}", city);
        Weather weather = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(HttpHeaders.EMPTY),
                Weather.class,
                city,
                apiKey
        ).getBody();
        logger.debug("the weather in the city {} is: {}", city, weather);
        return weather;
    }

    @Override
    public boolean isMessageUnique(long chatId, String city, LocalDateTime time) {
        WeatherNotification existingNotification = weatherNotificationRepository.findByChatIdAndCityNameAndReminderDateTime(chatId, city, time);
        return existingNotification == null;
    }

    @Override
    public void save(WeatherNotification weatherNotification) {
        weatherNotificationRepository.save(weatherNotification);
    }

    @Override
    public List<WeatherNotification> findByTime(LocalDateTime currentDate) {
        return weatherNotificationRepository.findByReminderDateTime(currentDate);
    }

    @Override
    public void delete(WeatherNotification weatherNotification) {
        weatherNotificationRepository.delete(weatherNotification);
    }
}
