package ua.nick.weather.model;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class City {

    private long id;
    private String name;
    private String country;
    private Double lon;
    private Double lat;

    public City() {
    }

    public City(String name) {
        this.name = name;
    }

    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public City(String name, String country, Double lon, Double lat) {
        this.name = name;
        this.country = country;
        this.lon = lon;
        this.lat = lat;
    }

    public City(String name, Double lon, Double lat) {
        this.name = name;
        this.lon = lon;
        this.lat = lat;
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

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String textNameCountry() {
        return this.name + "," + this.country;
    }
}

