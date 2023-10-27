package pro.sky.telegrambot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "weather_notification")
public class WeatherNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String cityName;
    LocalDateTime reminderDateTime;

    public WeatherNotification(Long chatId, String cityName, LocalDateTime reminderDateTime) {
        this.chatId = chatId;
        this.cityName = cityName;
        this.reminderDateTime = reminderDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeatherNotification)) return false;
        WeatherNotification that = (WeatherNotification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
