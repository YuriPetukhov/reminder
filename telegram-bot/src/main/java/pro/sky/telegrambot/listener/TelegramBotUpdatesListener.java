package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.enums.Commands;
import pro.sky.telegrambot.enums.UserState;
import pro.sky.telegrambot.service.MessageService;
import pro.sky.telegrambot.service.UserStateService;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor  // Generate a constructor requiring all final members
@Service  // Indicate that it's a service class (Spring framework annotation)
@Slf4j  // Lombok annotation for SLF4J logging
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final String EXIT_COMMAND = "отмена";   // Exit command constant

    // Injecting required dependencies
    private final TelegramBot telegramBot;
    private final MessageService messageService;
    private final UserStateService userStateService;

    @PostConstruct  // Annotation to indicate that this method should be run after dependency injection
    public void init() {
        telegramBot.setUpdatesListener(this); // Initialize bot with updates listener
    }


    @Override
    public int process(List<Update> updates) {  // Process incoming updates
        updates.forEach(update -> {  // Loop through updates
            log.info("Processing update: {}", update); // Logging an update for debugging purposes

            // Pulling details from the update
            String messageText = update.message().text();
            long chatId = update.message().chat().id();
            String chatFirstName = update.message().chat().firstName();
            // Obtain current user state from UserStateService
            UserState userState = userStateService.getUserState(chatId);

            // Checking if the message is an exit command
            if (messageText.equalsIgnoreCase(EXIT_COMMAND)) {
                userStateService.setUserState(chatId, UserState.IDLE); // Set user state to idle
                SendMessage message = new SendMessage(chatId, "Ввод отменен"); // Cancel input message
                telegramBot.execute(message); // Execute sending message
                messageService.sendCommandList(chatId); // Send a list of commands
                return;
            }

            // Check user state and act accordingly
            if (userState == UserState.AWAITING_WEATHER_CITY) {
                messageService.sendOrSaveWeatherMessageInCity(messageText, chatId); // Send/save weather message based on city
                return;
            }

            if (userState == UserState.AWAITING_NOTIFICATION) {
                messageService.saveNotificationMessage(messageText, chatId); // Save notification message
                return;
            }

            // Check if the message matches a command
            Commands command = Commands.fromString(messageText);

            if (command != null) { // If command exists, handle command according to its type
                switch (command) {
                    case START:
                        log.info("Received START command");  // Log command type
                        messageService.sendWelcomeMessage(chatFirstName, chatId);  // Send welcome message
                        messageService.sendCommandList(chatId);  // Send command list
                        break;
                    case NOTIFICATION:
                        log.info("Received NOTIFICATION command");  // Log command type
                        messageService.sendNotificationMessage(messageText, chatId);  // Send notification message
                        break;
                    case WEATHER:
                        log.info("Received WEATHER command");  // Log command type
                        messageService.sendWeatherMessage(chatFirstName, chatId);  // Send weather message
                        break;
                    case HELP:
                        log.info("Received HELP command");  // Log command type
                        messageService.sendHelpMessage(chatId);  // Send help message
                        break;
                }
            } else {  // If there's no command, then send a default message
                messageService.sendDefaultMessage(chatId);
            }
        });

        log.info("Updates processed");  // Indicate that all updates were processed
        return UpdatesListener.CONFIRMED_UPDATES_ALL;  // Return confirmation of all updates processed
    }
}
