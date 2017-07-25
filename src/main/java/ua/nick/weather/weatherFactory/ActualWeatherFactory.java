package ua.nick.weather.weatherFactory;

import org.springframework.stereotype.Component;
import ua.nick.weather.model.Forecast;
import ua.nick.weather.model.Provider;
import ua.nick.weather.utils.ParseUtils;

import java.text.ParseException;

@Component
public class ActualWeatherFactory {

    private ParseUtils parseUtils;

    public ActualWeatherFactory() {
        this.parseUtils = new ParseUtils();
    }

    public Forecast createActualModelFromJson(Provider provider, long cityId, String json) throws ParseException {

        Forecast actual = new Forecast(provider, true);

        if (Provider.OPENWEATHER == provider) {
            actual = parseUtils.parseActualWeatherFromOpenWeather(actual, cityId, json);

        } else if (Provider.WUNDERGROUND == provider) {
            actual = parseUtils.parseActualWeatherFromWunderground(actual, cityId, json);

        } else if (Provider.FORECA == provider) {
            actual = parseUtils.parseActualWeatherFromForeca(actual, cityId, json);

        } else if (Provider.DARK_SKY == provider) {
            actual = parseUtils.parseActualWeatherFromDarkSky(actual, cityId, json);
        }

        return actual;
    }
}