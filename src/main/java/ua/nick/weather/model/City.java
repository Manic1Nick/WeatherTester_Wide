package ua.nick.weather.model;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class City {

    private long id;
    private String name;
    private String country;
    private Double lat;
    private Double lon;

    public City() {
    }

    public City(String name) {
        this.name = name;
    }

    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public City(String name, String country, Double lat, Double lon) {
        this.name = name;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public City(String name, Double lat, Double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Id
    @SequenceGenerator(name="CITIES_SEQ_GEN", sequenceName="CITIES_SEQ", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CITIES_SEQ_GEN")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String textNameCountry() {
        return this.name + "," + this.country;
    }
}

