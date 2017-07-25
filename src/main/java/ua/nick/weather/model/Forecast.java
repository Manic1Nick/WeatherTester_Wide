package ua.nick.weather.model;

import javax.persistence.*;

@Entity
@Table(name = "forecasts")
public class Forecast {

    private Long id;
    private long timeUnix;
    private String date;
    private int tempMin;
    private int tempMax;
    private int pressure;
    private int clouds;
    private int windSpeed;
    private String description;
    private Provider provider;
    private int daysBeforeActual;
    private boolean actual; //false for forecast, true for fact weather

    private long cityId;

    public Forecast() {
    }

    public Forecast(Provider provider) {
        this.provider = provider;
    }

    public Forecast(Provider provider, boolean actual) {
        this.provider = provider;
        this.actual = actual;
    }

    public Forecast(Provider provider, boolean actual, long cityId) {
        this.provider = provider;
        this.actual = actual;
        this.cityId = cityId;
    }

    public Forecast(long timeUnix, String date, int tempMin, int tempMax, int pressure, int clouds, int windSpeed, String description, Provider provider, int daysBeforeActual, boolean actual) {
        this.timeUnix = timeUnix;
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.description = description;
        this.provider = provider;
        this.daysBeforeActual = daysBeforeActual;
        this.actual = actual;
    }

    @Id
    @SequenceGenerator(name="FORECASTS_SEQ_GEN", sequenceName="FORECASTS_SEQ", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FORECASTS_SEQ_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTimeUnix() {
        return timeUnix;
    }

    public void setTimeUnix(long timeUnix) {
        this.timeUnix = timeUnix;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public int getDaysBeforeActual() {
        return daysBeforeActual;
    }

    public void setDaysBeforeActual(int daysBeforeActual) {
        this.daysBeforeActual = daysBeforeActual;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "date='" + date + '\'' +
                ", tempMin=" + tempMin +
                ", tempMax=" + tempMax +
                ", pressure=" + pressure +
                ", clouds=" + clouds +
                ", windSpeed=" + windSpeed +
                ", description='" + description + '\'' +
                ", provider=" + provider +
                ", daysBeforeActual=" + daysBeforeActual +
                ", actual=" + actual +
                '}';
    }

    @Transient
    public String getTextValueByString(String item) {
        String value = "";
        item = item.toLowerCase();

        if ("date".equals(item))
            value = date;
        else if ("temp".equals(item))
            value = String.valueOf(Math.round((tempMin + tempMax) / 2));
        else if ("pressure".equals(item))
            value = String.valueOf(pressure);
        else if ("clouds".equals(item))
            value = String.valueOf(clouds);
        else if ("windspeed".equals(item))
            value = String.valueOf(windSpeed);
        else if ("description".equals(item))
            value = description;

        return value;
    }

    public String forecastOrActual() {
        if (this.isActual())
            return "actual";
        else
            return "forecast";
    }
}
