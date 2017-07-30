package ua.nick.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.nick.weather.model.City;
import ua.nick.weather.repository.CityRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public City getCityById(Long id) {
        return cityRepository.findById(id);
    }

    //todo user -> password -> city?
    @Override
    public City determineCity() {
        return null;
    }

    @Override
    public City getCityFromPlace(String addressText, Double lat, Double lng) {

        City city = parsePlace(addressText, lat, lng);

        if (getCityFromDB(city) == null)
            saveNewCity(city);

        return getCityFromDB(city);
    }

    @Override
    public List<String> getAllCitiesNames() {
        List<City> cities = cityRepository.findAll();

        return cities.stream().map(City::textNameCountry).collect(Collectors.toList());
    }

    private long saveNewCity(City city) {
        cityRepository.save(city);

        return getCityIdByNameAndCountry(city.getName(), city.getCountry());
    }

    private long getCityIdByNameAndCountry(String name, String country) {
        City city = cityRepository.findByNameAndCountry(name, country);

        if (city != null)
            return city.getId();

        return -1;
    }

    private City parsePlace(String cityText, Double lat, Double lng) {
        String[] cityArray = cityText.split(",");
        String name = cityArray[0].trim();
        String country = cityArray[cityArray.length - 1].trim();

        return new City(name, country, lat, lng);
    }

    private City getCityFromDB(City city) {
        City cityInDB;

        cityInDB = getCityByNameAndCountry(city.getName(), city.getCountry());
        if (cityInDB != null)
            return cityInDB;

        cityInDB = getCityByLatAndLon(city.getLat(), city.getLon());
        if (cityInDB != null)
            return cityInDB;

        return null;
    }

    private City getCityByNameAndCountry(String name, String country) {
        return cityRepository.findByNameAndCountry(name, country);
    }

    private City getCityByLatAndLon(double lat, double lng) {
        return cityRepository.findByLatAndLon(lat, lng);
    }
}
