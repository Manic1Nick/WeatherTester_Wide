package ua.nick.weather.weatherFactory;

import org.springframework.stereotype.Component;
import ua.nick.weather.model.Forecast;
import ua.nick.weather.model.Provider;
import ua.nick.weather.utils.ParseUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ForecastFactory {

    private ParseUtils parseUtils;

    public ForecastFactory() {
        this.parseUtils = new ParseUtils();
    }

    public List<Forecast> createListForecastsFromJsonByProvider(
            Provider provider, long cityId, String json) throws ParseException {

        List<Forecast> forecasts = new ArrayList<>();

        if (Provider.OPENWEATHER == provider) {
            forecasts = parseUtils.parseForecastsFromOpenWeather(forecasts, cityId, json);

        } else if (Provider.WUNDERGROUND == provider) {
            forecasts = parseUtils.parseForecastsFromWunderground(forecasts, cityId, json);

        } else if (Provider.FORECA == provider) {
            forecasts = parseUtils.parseForecastsFromForeca(forecasts, cityId, json);

        } else if (Provider.DARK_SKY == provider) {
            forecasts = parseUtils.parseForecastsFromDarkSky(forecasts, cityId, json);
        }

        return forecasts;
    }
}
