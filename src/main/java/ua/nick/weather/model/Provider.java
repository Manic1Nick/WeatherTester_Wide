package ua.nick.weather.model;

import java.util.Arrays;
import java.util.List;

public enum Provider {

    OPENWEATHER(
            "http://api.openweathermap.org/data/2.5/forecast/daily?q={city},{country}&units=metric&appid=e962ca86a64e342603c7da6c403847d6",
            "http://api.openweathermap.org/data/2.5/weather?q={city},{country}&units=metric&appid=e962ca86a64e342603c7da6c403847d6",
            "small_openweathermap_logo.png",
            "row_openweathermap_logo.png",
            "#ebea2f",
            7,
            false
    ),

    WUNDERGROUND(
            "http://api.wunderground.com/api/8a173cdd18d0cabe/forecast/q/{lng},{lat}.json",
            "http://api.wunderground.com/api/8a173cdd18d0cabe/conditions/q/{lng},{lat}.json",
            "small_wu_logo.png",
            "row_wu_logo.png",
            "#9d9ad1",
            4,
            false
    ),

    FORECA(
            "http://apitest.foreca.net/?lon={lat}&lat={lng}&key=IT7YtSoC0tgh3Chl0PHaZmb7g&format=json",
            "http://apitest.foreca.net/?lon={lat}&lat={lng}&key=IT7YtSoC0tgh3Chl0PHaZmb7g&format=json",
            "small_foreca_logo.png",
            "row_foreca_logo.png",
            "#29bfe4",
            10,
            true
    ),

    DARK_SKY(
            "https://api.darksky.net/forecast/030645a49019b80fcc061e3505f1b905/{lng},{lat}",
            "https://api.darksky.net/forecast/030645a49019b80fcc061e3505f1b905/{lng},{lat}",
            "small_darksky_logo.png",
            "row_darksky_logo.png",
            "#29e45c",
            8,
            false
    )/*,

    APIXU(
            "http://api.apixu.com/v1/forecast.json?key=03863e31405349848b9102935170104&q={city}",
            "http://api.apixu.com/v1/current.json?key=03863e31405349848b9102935170104&q={city}",
            "small_darksky_logo.png",
            "row_darksky_logo.png",
            "#e43e29",
            8
    )*/;

    //next colors: "#f19729", "#29f1d2"

    private String name = this.name();
    private int number = this.ordinal();

    //private static final Map<String, String> keyApiMap = Constants.PROVIDERS_KEYS_API_MAP;

    private String linkForecast;
    private String linkActual;
    private String logo;
    private String rowLogo;
    private String color;
    private int maxDaysForecast;
    private boolean expandedJson;

    Provider() {
    }

    Provider(String linkForecast, String linkActual, String logo, String rowLogo, String color,
             int maxDaysForecast, boolean expandedJson) {
        this.linkForecast = linkForecast;
        this.linkActual = linkActual;
        this.logo = logo;
        this.rowLogo = rowLogo;
        this.color = color;
        this.maxDaysForecast = maxDaysForecast;
        this.expandedJson = expandedJson;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getLinkForecast() {
        return linkForecast;
    }

    public String getLinkActual() {
        return linkActual;
    }

    public String getLogo() {
        return logo;
    }

    public String getRowLogo() {
        return rowLogo;
    }

    public String getColor() {
        return color;
    }

    public int getMaxDaysForecast() {
        return maxDaysForecast;
    }

    //some providers have 1 json for all forecasts and actual (and has limit for free connection)
    public boolean hasExpandedJson() {
        return expandedJson;
    }

    public static List<Provider> getAll() {
        return Arrays.asList(values());
    }

    public static int lengthAll() {
        return values().length;
    }

    public static int lengthWithoutExpandedJson() {
        int count = 0;
        for (Provider provider : Arrays.asList(values()))
            count += provider.hasExpandedJson() ? 0 : 1 ;

        return count;
    }
}
