package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pro.sky.telegrambot.enums.*;
import pro.sky.telegrambot.model.*;
import pro.sky.telegrambot.model.weather.*;
import pro.sky.telegrambot.service.*;
import pro.sky.telegrambot.utils.*;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {
    private final WeatherService weatherService;
    private final NotificationService notificationService;
    private final UserStateService userStateService;
    private final TelegramBot telegramBot;
    private final DateAndTimeValidator dateAndTimeValidator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Pattern REGEX_PATTERN_WEATHER = Pattern.compile("([0-9\\.\\:\\s]{16})?(\\s)?(.+)");
    private static final Pattern REGEX_PATTERN_NOTIFICATION = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)(.+)");

    public void sendCommandList(long chatId) {
        sendHTMLMessage(chatId, MessageContent.COMMAND_LIST.getTemplate());
    }

    public void sendWelcomeMessage(String chatFirstName, long chatId) {
        logger.info("Sending welcome message to {}: {}", chatFirstName, chatId);
        sendHTMLMessage(chatId, MessageContent.WELCOME_MESSAGE.format(chatFirstName));
    }

    @Override
    public void sendNotificationMessage(String messageText, long chatId) {
        logger.info("Sending notification message - {}: {}", messageText, chatId);
        sendMessage(chatId, MessageContent.WHEN_ABOUT_QUESTION.getTemplate());
        userStateService.setUserState(chatId, UserState.AWAITING_NOTIFICATION);
    }

    @Override
    public void saveNotificationMessage(String messageText, long chatId) {
        Matcher matcher = REGEX_PATTERN_NOTIFICATION.matcher(messageText);

        if (!matcher.find()) {
            sendMessage(chatId, MessageContent.MESSAGE_FORMAT_ERROR_WARNING.getTemplate());
            userStateService.setUserState(chatId, UserState.AWAITING_NOTIFICATION);
            return;
        }

        String dateTimeString = matcher.group(1);
        String reminderText = matcher.group(3);

        LocalDateTime reminderDateTime = dateAndTimeValidator.validateAndConvert(
                dateTimeString, chatId,
                UserState.AWAITING_NOTIFICATION);
        if (reminderDateTime == null) {
            return;
        }
        Notification notification = new Notification(chatId, reminderText, reminderDateTime);

        if (dateAndTimeValidator.checkDateTime(reminderDateTime, chatId, UserState.AWAITING_NOTIFICATION)) {
            sendHTMLMessage(chatId, MessageContent.WRONG_TIME_WARNING.getTemplate());
            return;
        }

        if (!notificationService.isMessageUnique(chatId, reminderText, reminderDateTime)) {
            sendHTMLMessage(chatId, MessageContent.DUPLICATE_WARNING.getTemplate());
            userStateService.setUserState(chatId, UserState.AWAITING_NOTIFICATION);
            return;
        }

        notificationService.save(notification);
        sendMessage(chatId, MessageContent.WAIT_MESSAGE.getTemplate());
        userStateService.setUserState(chatId, UserState.IDLE);
        sendCommandList(chatId);
    }

    @Override
    public void sendWeatherMessage(String chatFirstName, long chatId) {
        sendHTMLMessage(chatId, MessageContent.WEATHER_FORECAST_QUESTION.format(chatFirstName));
        userStateService.setUserState(chatId, UserState.AWAITING_WEATHER_CITY);
    }

    @Override
    public void sendOrSaveWeatherMessageInCity(String messageText, long chatId) {
        logger.info("Sending weather message in city - {}: {}", messageText, chatId);
        Matcher matcher = REGEX_PATTERN_WEATHER.matcher(messageText);

        if (matcher.find()) {
            String dateTimeString = matcher.group(1);
            String cityName = matcher.group(3);

            if (dateTimeString == null && cityName != null) {
                try {
                    Weather weather = weatherService.getWeather(cityName);
                    sendMessage(chatId, MessageContent.WEATHER_FORECAST_ANSWER.format(cityName, weather));
                    sendCommandList(chatId);
                } catch (HttpClientErrorException.NotFound e) {
                    logger.error("City not found: ", e);
                    sendMessage(chatId, MessageContent.CITY_NOT_FOUND_WARNING.format(cityName));
                    userStateService.setUserState(chatId, UserState.AWAITING_WEATHER_CITY);
                    return;
                }
            } else if (dateTimeString != null && cityName != null) {
                LocalDateTime reminderDateTime = dateAndTimeValidator.validateAndConvert(
                        dateTimeString, chatId,
                        UserState.AWAITING_WEATHER_CITY);
                if (reminderDateTime == null) {
                    return;
                }

                if (dateAndTimeValidator.checkDateTime(reminderDateTime, chatId, UserState.AWAITING_WEATHER_CITY)) {
                    sendHTMLMessage(chatId, MessageContent.WRONG_TIME_WARNING.getTemplate());
                    return;
                }

                if (!weatherService.isMessageUnique(chatId, cityName, reminderDateTime)) {
                    sendHTMLMessage(chatId, MessageContent.DUPLICATE_WARNING.getTemplate());
                    userStateService.setUserState(chatId, UserState.AWAITING_WEATHER_CITY);
                    return;
                }


                WeatherNotification weatherNotification = new WeatherNotification(chatId, cityName, reminderDateTime);
                weatherService.save(weatherNotification);
                sendMessage(chatId, MessageContent.WAIT_MESSAGE.getTemplate());
                sendCommandList(chatId);
            }
        }

        userStateService.setUserState(chatId, UserState.IDLE);
    }

    @Override
    public void sendHelpMessage(long chatId) {
        sendHTMLMessage(chatId, MessageContent.HELP_MESSAGE.getTemplate());
    }

    @Override
    public void sendDefaultMessage(long chatId) {
        sendMessage(chatId, MessageContent.DEFAULT_MESSAGE.getTemplate());
        sendCommandList(chatId);
    }
    private void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        telegramBot.execute(message);
    }

    private void sendHTMLMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText).parseMode(ParseMode.HTML);
        telegramBot.execute(message);
    }

}