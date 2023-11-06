package pro.sky.telegrambot.model.weather;

import lombok.Data;

@Data
public class Weather {
    private WeatherMain main;
    private WeatherWind wind;

    public Weather() {
    }

    public Weather(WeatherMain main, WeatherWind wind) {
        this.main = main;
        this.wind = wind;
    }

    @Override
    public String toString() {
        return " " + main + ", " + wind;
    }
}
