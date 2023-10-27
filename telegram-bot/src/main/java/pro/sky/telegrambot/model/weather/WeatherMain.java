package pro.sky.telegrambot.model.weather;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class WeatherMain {
    private BigDecimal temp;
    private BigDecimal humidity;

    public WeatherMain() {
    }

    @Override
    public String toString() {
        return "температура = " + temp +
                ", влажность = " + humidity;
    }
}
