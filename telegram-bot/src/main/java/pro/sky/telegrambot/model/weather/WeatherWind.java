package pro.sky.telegrambot.model.weather;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class WeatherWind {
    private BigDecimal speed;
    private Integer deg;

    public WeatherWind() {
    }

    @Override
    public String toString() {
            return "скорость ветра = " + speed +
                    ", направление ветра = " + deg;
    }
}
