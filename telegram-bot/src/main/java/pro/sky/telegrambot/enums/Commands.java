package pro.sky.telegrambot.enums;

import lombok.Getter;

@Getter
public enum Commands {
    START("/start"),
    NOTIFICATION("/notification"),
    WEATHER("/weather"),
    HELP("/help");

    private final String text;

    Commands(String text) {
        this.text = text;
    }

    public static Commands fromString(String text) {
        for (Commands b : Commands.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

}
