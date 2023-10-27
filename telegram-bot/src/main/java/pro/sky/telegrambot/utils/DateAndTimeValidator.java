package pro.sky.telegrambot.utils;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.enums.MessageContent;
import pro.sky.telegrambot.service.UserStateService;
import pro.sky.telegrambot.enums.UserState;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class DateAndTimeValidator {

    private final static Logger logger = LoggerFactory.getLogger(DateAndTimeValidator.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final UserStateService userStateService;


    public LocalDateTime validateAndConvert (String dateTimeString, long chatId, UserState userState) {
        try {
            return LocalDateTime.parse(dateTimeString.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            String text = MessageContent.TIME_FORMAT_ERROR_WARNING.getTemplate();
            userStateService.setUserState(chatId, UserState.AWAITING_NOTIFICATION);
            logger.warn("Time format error: ", ex);
            return null;
        }
    }

    public boolean checkDateTime(LocalDateTime reminderDateTime, long chatId, UserState userState) {
        LocalDateTime nowPlusFiveMinutes = LocalDateTime.now().plusMinutes(5);
        if (reminderDateTime.isBefore(nowPlusFiveMinutes)) {
            userStateService.setUserState(chatId, userState);
            return true;
        }
        return false;
    }
}

