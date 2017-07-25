package ua.nick.weather.modelTester;

import ua.nick.weather.model.City;
import ua.nick.weather.model.Provider;

public class TesterItem {

    private String name;
    private Provider provider;
    private String date;
    private String valueForecast;
    private String valueActual;
    private String valueDiff;

    private City city;

    public TesterItem() {
    }

    public TesterItem(String name, Provider provider, String date, String valueForecast, String valueActual, String valueDiff) {
        this.name = name;
        this.provider = provider;
        this.date = date;
        this.valueForecast = valueForecast;
        this.valueActual = valueActual;
        this.valueDiff = valueDiff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValueForecast() {
        return valueForecast;
    }

    public void setValueForecast(String valueForecast) {
        this.valueForecast = valueForecast;
    }

    public String getValueActual() {
        return valueActual;
    }

    public void setValueActual(String valueActual) {
        this.valueActual = valueActual;
    }

    public String getValueDiff() {
        return valueDiff;
    }

    public void setValueDiff(String valueDiff) {
        this.valueDiff = valueDiff;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
