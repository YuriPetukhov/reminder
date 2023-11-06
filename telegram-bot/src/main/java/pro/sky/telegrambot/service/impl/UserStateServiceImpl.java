package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.enums.UserState;
import pro.sky.telegrambot.service.UserStateService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J logging
public class UserStateServiceImpl implements UserStateService {
    // Using a HashMap to store the different states of the users
    private final Map<Long, UserState> userStates = new HashMap<>();

    // Method to get the current state of a user based on their userId
    public UserState getUserState(Long userId) {
        UserState userState = userStates.getOrDefault(userId, UserState.IDLE);
        log.info("Returning user state for userId: {}. State: {}", userId, userState);
        return userState;
    }

    // Method to change the state of a user based on their userId
    public void setUserState(Long userId, UserState userState) {
        userStates.put(userId, userState);
        log.info("Set state for userId: {} to: {}", userId, userState);
    }
}
