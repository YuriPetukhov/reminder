package pro.sky.telegrambot.model.weather;

import lombok.Data;

import java.math.BigDecimal;
@Data
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
