package pro.sky.telegrambot.enums;

import lombok.Getter;

@Getter
public enum Command {
    START("/start"),
    NOTIFICATION("/notification"),
    WEATHER("/weather"),
    HELP("/help");

    private final String text;

    Command(String text) {
        this.text = text;
    }

    public static Command fromString(String text) {
        for (Command b : Command.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

}
