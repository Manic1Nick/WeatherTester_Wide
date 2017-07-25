package ua.nick.weather.weatherFactory;

import org.springframework.stereotype.Component;
import ua.nick.weather.model.City;
import ua.nick.weather.model.Provider;

@Component
public class LinkForecastsFactory {

    public LinkForecastsFactory() {
    }

    public String createLinkForecastsForProviderByCity(Provider provider, City city, boolean actual) {

        String link = actual ? provider.getLinkActual() : provider.getLinkForecast();

        if (Provider.OPENWEATHER == provider) {
            link = link.replace("{city}", getCityName(city));
            link = link.replace("{country}", getCityCountry(city));

        } else if (Provider.WUNDERGROUND == provider) {
            link = link.replace("{lng}", getCityLng(city));
            link = link.replace("{lat}", getCityLat(city));

        } else if (Provider.FORECA == provider) {
            link = link.replace("{lng}", getCityLng(city));
            link = link.replace("{lat}", getCityLat(city));

        } else if (Provider.DARK_SKY == provider) {
            link = link.replace("{lng}", getCityLng(city));
            link = link.replace("{lat}", getCityLat(city));
        }
        return link;
    }

    private String getCityName(City city) {
        return city.getName().replace(" ", "");
    }

    private String getCityCountry(City city) {
        String country = city.getCountry();

        if (country.contains(" ")) {
            String[] array = country.split(" ");
            String countryAbbr = "";
            for (String str : array)
                countryAbbr += str.substring(0, 1);

            country = countryAbbr;
        }
        return country;
    }

    private String getCityLat(City city) {
        Double lat = city.getLat(); //todo make round 0.0000
        return lat.toString();
    }

    private String getCityLng(City city) {
        Double lng = city.getLon(); //todo make round 0.0000
        return lng.toString();
    }
}
