package pro.sky.telegrambot.model.weather;

import lombok.Data;

import java.math.BigDecimal;
@Data
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
