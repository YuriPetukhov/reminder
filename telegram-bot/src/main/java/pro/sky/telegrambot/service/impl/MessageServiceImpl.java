package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

// Initialization of the service with all required parameters
@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J logging
public class MessageServiceImpl implements MessageService {
    private final WeatherService weatherService;
    private final NotificationService notificationService;
    private final UserStateService userStateService;
    private final TelegramBot telegramBot;
    private final DateAndTimeService dateAndTimeService;

    // Regex patterns for message validation
    private static final Pattern REGEX_PATTERN_WEATHER = Pattern.compile("([0-9\\.\\:\\s]{16})?(\\s)?(.+)");
    private static final Pattern REGEX_PATTERN_NOTIFICATION = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)(.+)");

    // Sending a list of available commands to the chat
    @Override
    public void sendCommandList(long chatId) {
        log.info("Sending command list to chatId: {}", chatId);
        sendHTMLMessage(chatId, MessageContent.COMMAND_LIST.getTemplate());
    }

    // Sending a welcome message to the chat
    public void sendWelcomeMessage(String chatFirstName, long chatId) {
        log.info("Sending welcome message to {}: {}", chatFirstName, chatId);
        sendHTMLMessage(chatId, MessageContent.WELCOME_MESSAGE.format(chatFirstName));
    }

    // Sending a question to the chat regarding when and what type of notifications the user needs
    @Override
    public void sendNotificationMessage(String messageText, long chatId) {
        log.info("Sending notification message: '{}' to chatId: {}", messageText, chatId);
        sendMessage(chatId, MessageContent.WHEN_ABOUT_QUESTION.getTemplate());
        userStateService.setUserState(chatId, UserState.AWAITING_NOTIFICATION);
    }

    /* Implementing the logic of saving a notification message including:
       regex validation, date and time validation, checking for message uniqueness
       and saving the notification */
    @Override
    public void saveNotificationMessage(String messageText, long chatId) {
        log.info("Begin saveNotificationMessage - Message: {}, ChatId: {}", messageText, chatId);
        Matcher matcher = REGEX_PATTERN_NOTIFICATION.matcher(messageText);

        if (!matcher.find()) {
            log.info("Message format error found - Message: {}, ChatId: {}", messageText, chatId);
            sendMessage(chatId, MessageContent.MESSAGE_FORMAT_ERROR_WARNING.getTemplate());
            userStateService.setUserState(chatId, UserState.AWAITING_NOTIFICATION);
            return;
        }

        String dateTimeString = matcher.group(1);
        String reminderText = matcher.group(3);
        log.info("Parsed notification message - DateTime: {}, ReminderText: {}", dateTimeString, reminderText);

        LocalDateTime reminderDateTime = dateAndTimeService.validateAndConvert(
                dateTimeString, chatId,
                UserState.AWAITING_NOTIFICATION);
        if (reminderDateTime == null) {
            log.warn("Failed to convert date and time - DateTime: {}, ChatId: {}", dateTimeString, chatId);
            return;
        }

        Notification notification = new Notification(chatId, reminderText, reminderDateTime);
        log.info("Created notification object - Notification: {}, ChatId: {}", notification, chatId);

        if (dateAndTimeService.checkDateTime(reminderDateTime, chatId, UserState.AWAITING_NOTIFICATION)) {
            log.info("Detected wrong time - DateTime: {}, ChatId: {}", reminderDateTime, chatId);
            sendHTMLMessage(chatId, MessageContent.WRONG_TIME_WARNING.getTemplate());
            return;
        }

        if (!notificationService.isMessageUnique(chatId, reminderText, reminderDateTime)) {
            log.info("Detected duplicate message - Message: {}, DateTime: {}, ChatId: {}",
                    reminderText, reminderDateTime, chatId);
            sendHTMLMessage(chatId, MessageContent.DUPLICATE_WARNING.getTemplate());
            userStateService.setUserState(chatId, UserState.AWAITING_NOTIFICATION);
            return;
        }

        notificationService.save(notification);
        log.info("Notification saved - Notification: {}, ChatId: {}", notification, chatId);
        sendMessage(chatId, MessageContent.WAIT_MESSAGE.getTemplate());
        userStateService.setUserState(chatId, UserState.IDLE);
        sendCommandList(chatId);

        log.info("End saveNotificationMessage - Message: {}, ChatId: {}", messageText, chatId);
    }

    //  Sending a message to the chat asking which city's weather forecast the user needs
    @Override
    public void sendWeatherMessage(String chatFirstName, long chatId) {
        log.info("Executing sendWeatherMessage to- {}: {}", chatFirstName, chatId);
        sendHTMLMessage(chatId, MessageContent.WEATHER_FORECAST_QUESTION.format(chatFirstName));
        userStateService.setUserState(chatId, UserState.AWAITING_WEATHER_CITY);
    }

    /* Implementing the logic of sending a weather forecast or saving a weather notification,
       including message validation, date and time validation, uniqueness check
       and saving the weather notification */
    @Override
    public void sendOrSaveWeatherMessageInCity(String messageText, long chatId) {
        log.info("Sending weather message in city - {}: {}", messageText, chatId);
        Matcher matcher = REGEX_PATTERN_WEATHER.matcher(messageText);

        if (matcher.find()) {
            String dateTimeString = matcher.group(1);
            String cityName = matcher.group(3);

            if (dateTimeString == null && cityName != null) {
                log.info("Attempting to get weather for city: {}", cityName);
                try {
                    Weather weather = weatherService.getWeather(cityName);
                    sendMessage(chatId, MessageContent.WEATHER_FORECAST_ANSWER.format(cityName, weather));
                    sendCommandList(chatId);
                } catch (HttpClientErrorException.NotFound e) {
                    log.error("City not found: ", e);
                    sendMessage(chatId, MessageContent.CITY_NOT_FOUND_WARNING.format(cityName));
                    userStateService.setUserState(chatId, UserState.AWAITING_WEATHER_CITY);
                    return;
                }
                log.info("Successfully got weather for city: {}", cityName);
            } else if (dateTimeString != null && cityName != null) {
                LocalDateTime reminderDateTime = dateAndTimeService.validateAndConvert(
                        dateTimeString, chatId,
                        UserState.AWAITING_WEATHER_CITY);
                if (reminderDateTime == null) {
                    return;
                }

                if (dateAndTimeService.checkDateTime(reminderDateTime, chatId, UserState.AWAITING_WEATHER_CITY)) {
                    sendHTMLMessage(chatId, MessageContent.WRONG_TIME_WARNING.getTemplate());
                    return;
                }

                if (!weatherService.isMessageUnique(chatId, cityName, reminderDateTime)) {
                    sendHTMLMessage(chatId, MessageContent.DUPLICATE_WARNING.getTemplate());
                    userStateService.setUserState(chatId, UserState.AWAITING_WEATHER_CITY);
                    return;
                }

                log.info("Creating a WeatherNotification for chatId {}, cityName {}, reminderDateTime {}",
                        chatId, cityName, reminderDateTime);

                WeatherNotification weatherNotification = new WeatherNotification(chatId, cityName, reminderDateTime);
                weatherService.save(weatherNotification);
                sendMessage(chatId, MessageContent.WAIT_MESSAGE.getTemplate());
                sendCommandList(chatId);

                log.info("Successfully created a weather notification.");
            }
        }

        userStateService.setUserState(chatId, UserState.IDLE);
        log.info("Setting user state to IDLE for chatId: {}", chatId);
    }

    // Sending a help message and commands description to chat
    @Override
    public void sendHelpMessage(long chatId) {
        log.info("Executing sendHelpMessage to chatId: {}", chatId);
        sendHTMLMessage(chatId, MessageContent.HELP_MESSAGE.getTemplate());
    }

    // Sending a default message to chat followed by the command list
    @Override
    public void sendDefaultMessage(long chatId) {
        log.info("Executing sendDefaultMessage to chatId: {}", chatId);
        sendMessage(chatId, MessageContent.DEFAULT_MESSAGE.getTemplate());
        sendCommandList(chatId);
    }

    // General method for sending a message to chat
    private void sendMessage(long chatId, String messageText) {
        log.info("Executing sendMessage to chatId: {}", chatId);
        SendMessage message = new SendMessage(chatId, messageText);
        telegramBot.execute(message);
    }

    // General method for sending a message with HTML content to chat
    private void sendHTMLMessage(long chatId, String messageText) {
        log.info("Executing sendHTMLMessage to chatId: {}", chatId);
        SendMessage message = new SendMessage(chatId, messageText).parseMode(ParseMode.HTML);
        telegramBot.execute(message);
    }

}