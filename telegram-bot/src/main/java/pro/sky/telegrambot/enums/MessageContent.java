package pro.sky.telegrambot.enums;

import lombok.Getter;

@Getter
public enum MessageContent {
    COMMAND_LIST("<b>/start</b> - <i>поздороваться с ботом</i>\n\n"
            + "<b>/notification</b> - <i>установить напоминание</i>\n\n"
            + "<b>/weather</b> - <i>узнать погоду сейчас или в определенное время</i>\n\n"
            + "<b>/help</b> - <i>получить справку</i>"),

    WELCOME_MESSAGE("<b>Привет, %s!</b>"),

    HELP_MESSAGE("<b>Данный сервис позволяет установить напоминание,\n"
            + "а также узнать прогноз погоды в указанном городе.</b>\n\n"
            + "1. <b>/start</b> - <i>получить список всех доступных команд</i>\n\n"
            + "2. <b>/notification</b> - <i>установить напоминание на определенную дату</i>\n\n"
            + "3. <b>/weather</b> - <i>узнать прогноз погоды сейчас или в определенное время</i>\n\n"
            + "4. <b>/help</b> - <i>ознакомиться с этой информацией</i>\n\n"
            + "5. <i>Чтобы отменить действие и вернуться в меню, введи</i> <b>отмена</b>"),

    MESSAGE_FORMAT_ERROR_WARNING("Неверный формат сообщения.\n\n"
            + "Пример сообщения: \n"
            + "'01.01.2024 20:00 Сообщение'\n\n"
            + "Попробуй еще раз...\n\n "
            + "или напиши отмена"),

    TIME_FORMAT_ERROR_WARNING("Не могу обработать дату и время.\n"
            + "Убедитесь, что они в правильном формате \n"
            + "dd.MM.yyyy HH:mm -> <b>01.01.2024 20:00</b>\n\n"
            + "Попробуй еще раз...\n\n"
            + "или напиши отмена"),

    WHEN_ABOUT_QUESTION("Когда и о чем напомнить?"),

    WAIT_MESSAGE("Ожидай сообщение!\n\n"),

    DEFAULT_MESSAGE("Такую команду пока не обрабатываем."),

    WRONG_TIME_WARNING("Время должно быть позднее текущего\n"
            + "как минимум на пять минут.\n\n"
            + "Попробуй еще раз...\n\n"
            + "или напиши отмена"),

    DUPLICATE_WARNING("Такое напоминание уже существует!\n\n"
            + "Попробуй еще раз...\n\n"
            + "или напиши отмена"),

    CITY_NOT_FOUND_WARNING("Город %s не найден.\n\n"
            + "Попробуй еще раз...\n\n "
            + "или напиши отмена"),

    WEATHER_FORECAST_QUESTION("%s, укажи <b>город</b>.\n"
            + "Или укажи <b>дату</b> и <b>город</b>"),

    WEATHER_FORECAST_ANSWER("%s: прогноз погоды: %s"),
    ;

    private final String template;

    MessageContent(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return String.format(template, args);
    }
}