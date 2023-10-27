package pro.sky.telegrambot.service;

public interface MessageService {
    void sendWelcomeMessage(String chatFirstName, long chatId);

    void sendNotificationMessage(String messageText, long chatId);

    void sendWeatherMessage(String chatFirstName, long chatId);

    void sendHelpMessage(long chatId);

    void sendDefaultMessage(long chatId);

    void sendCommandList(long chatId);

    void sendOrSaveWeatherMessageInCity(String messageText, long chatId);

    void saveNotificationMessage(String messageText, long chatId);
}
