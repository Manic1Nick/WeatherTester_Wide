package ua.nick.weather.modelWeather.wunderground.wForecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class High {

    @SerializedName("fahrenheit")
    @Expose
    private String fahrenheit;
    @SerializedName("celsius")
    @Expose
    private String celsius;

    public String getFahrenheit() {
        return fahrenheit;
    }

    public void setFahrenheit(String fahrenheit) {
        this.fahrenheit = fahrenheit;
    }

    public String getCelsius() {
        return celsius;
    }

    public void setCelsius(String celsius) {
        this.celsius = celsius;
    }

}
