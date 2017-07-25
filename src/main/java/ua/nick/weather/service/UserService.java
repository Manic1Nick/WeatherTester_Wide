package ua.nick.weather.service;

import ua.nick.weather.model.City;

public interface UserService {

    long saveNewCity(City city);
    long getCityIdByNameAndCountry(String name, String country);
    City getCityById(Long id);
    City determineCity();
    City getCityFromPlace(String cityText, Double lat, Double lng);
}
