package pro.sky.telegrambot.service;

import pro.sky.telegrambot.enums.UserState;

public interface UserStateService {
    UserState getUserState(Long userId);
    void setUserState(Long userId, UserState userState);
}
