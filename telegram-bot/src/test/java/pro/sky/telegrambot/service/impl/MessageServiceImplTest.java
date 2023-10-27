package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.enums.UserState;
import pro.sky.telegrambot.service.NotificationService;
import pro.sky.telegrambot.service.UserStateService;
import pro.sky.telegrambot.service.WeatherService;
import pro.sky.telegrambot.utils.DateAndTimeValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    private WeatherService weatherService;
    private NotificationService notificationService;
    private UserStateService userStateService;
    private TelegramBot telegramBot;
    private DateAndTimeValidator dateAndTimeValidator;

    private MessageServiceImpl messageService;

    @BeforeEach
    public void setup() {
        weatherService = Mockito.mock(WeatherService.class);
        notificationService = Mockito.mock(NotificationService.class);
        userStateService = Mockito.mock(UserStateService.class);
        telegramBot = Mockito.mock(TelegramBot.class);
        dateAndTimeValidator = Mockito.mock(DateAndTimeValidator.class);

        messageService = new MessageServiceImpl(weatherService, notificationService, userStateService, telegramBot, dateAndTimeValidator);
    }

    @Test
    public void sendCommandListTest() {
        long chatId = 123456789L;
        messageService.sendCommandList(chatId);

        verify(telegramBot, times(1)).execute(any(SendMessage.class));
    }

    @Test
    public void sendWelcomeMessageTest() {
        long chatId = 123456789L;
        String firstName = "Test";

        messageService.sendWelcomeMessage(firstName, chatId);

        verify(telegramBot, times(1)).execute(any(SendMessage.class));
    }

    @Test
    public void sendNotificationMessageTest() {
        long chatId = 123456789L;
        String messageText = "Test";

        messageService.sendNotificationMessage(messageText, chatId);

        verify(telegramBot, times(1)).execute(any(SendMessage.class));
        verify(userStateService, times(1)).setUserState(eq(chatId), any(UserState.class));
    }

    @Test
    public void sendHelpMessageTest() {
        long chatId = 123456789L;

        messageService.sendHelpMessage(chatId);

        verify(telegramBot, times(1)).execute(any(SendMessage.class));
    }

    @Test
    public void sendDefaultMessageTest() {
        long chatId = 123456789L;

        messageService.sendDefaultMessage(chatId);

        verify(telegramBot, times(2)).execute(any(SendMessage.class));
    }
}
