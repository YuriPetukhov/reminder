package pro.sky.telegrambot.utils;

import pro.sky.telegrambot.enums.UserState;

import java.time.LocalDateTime;

public interface DateAndTimeService {
    LocalDateTime validateAndConvert(String dateTimeString, long chatId, UserState userState);

    boolean checkDateTime(LocalDateTime reminderDateTime, long chatId, UserState userState);
}
