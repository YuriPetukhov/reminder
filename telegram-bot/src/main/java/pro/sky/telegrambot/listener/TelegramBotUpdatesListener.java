package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.enums.Commands;
import pro.sky.telegrambot.enums.UserState;
import pro.sky.telegrambot.service.MessageService;
import pro.sky.telegrambot.service.UserStateService;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final String EXIT_COMMAND = "отмена";

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TelegramBot telegramBot;

    private final MessageService messageService;

    private final UserStateService userStateService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            String messageText = update.message().text();
            long chatId = update.message().chat().id();
            String chatFirstName = update.message().chat().firstName();
            UserState userState = userStateService.getUserState(chatId);

            if (messageText.equalsIgnoreCase(EXIT_COMMAND)) {
                userStateService.setUserState(chatId, UserState.IDLE);
                SendMessage message = new SendMessage(chatId, "Ввод отменен");
                telegramBot.execute(message);
                messageService.sendCommandList(chatId);
                return;
            }

            if (userState == UserState.AWAITING_WEATHER_CITY) {
                messageService.sendOrSaveWeatherMessageInCity(messageText, chatId);
                return;
            }
            if (userState == UserState.AWAITING_NOTIFICATION) {
                messageService.saveNotificationMessage(messageText, chatId);
                return;
            }

            Commands command = Commands.fromString(messageText);

            if (command != null) {
                switch (command) {
                    case START:
                        logger.info("Received START command");
                        messageService.sendWelcomeMessage(chatFirstName, chatId);
                        messageService.sendCommandList(chatId);
                        break;
                    case NOTIFICATION:
                        logger.info("Received NOTIFICATION command");
                        messageService.sendNotificationMessage(messageText, chatId);
                        break;
                    case WEATHER:
                        logger.info("Received WEATHER command");
                        messageService.sendWeatherMessage(chatFirstName, chatId);
                        break;
                    case HELP:
                        logger.info("Received HELP command");
                        messageService.sendHelpMessage(chatId);
                        break;
                }
            }else {
                messageService.sendDefaultMessage(chatId);
            }
        });
        logger.info("Updates processed");
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
