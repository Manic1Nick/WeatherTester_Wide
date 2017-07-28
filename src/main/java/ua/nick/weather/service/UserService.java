package ua.nick.weather.service;

import ua.nick.weather.model.City;

import java.util.List;

public interface UserService {

    long saveNewCity(City city);
    long getCityIdByNameAndCountry(String name, String country);
    City getCityById(Long id);
    City determineCity();
    City getCityFromPlace(String cityText, Double lat, Double lng);

    List<String> getAllCitiesNames();
}
