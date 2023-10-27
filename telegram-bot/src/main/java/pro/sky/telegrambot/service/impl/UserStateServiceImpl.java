package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.enums.UserState;
import pro.sky.telegrambot.service.UserStateService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserStateServiceImpl implements UserStateService {
    private final Map<Long, UserState> userStates = new HashMap<>();

    public UserState getUserState(Long userId) {
        return userStates.getOrDefault(userId, UserState.IDLE);
    }

    public void setUserState(Long userId, UserState userState) {
        userStates.put(userId, userState);
    }
}
