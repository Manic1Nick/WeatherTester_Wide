package ua.nick.weather.modelWeather.wunderground.wForecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QpfAllday {

    @SerializedName("in")
    @Expose
    private Double in;
    @SerializedName("mm")
    @Expose
    private Integer mm;

    public Double getIn() {
        return in;
    }

    public void setIn(Double in) {
        this.in = in;
    }

    public Integer getMm() {
        return mm;
    }

    public void setMm(Integer mm) {
        this.mm = mm;
    }

}
