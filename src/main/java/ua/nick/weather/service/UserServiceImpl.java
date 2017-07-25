package ua.nick.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.nick.weather.model.City;
import ua.nick.weather.repository.CityRepository;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public long saveNewCity(City city) {
        cityRepository.save(city);

        return getCityIdByNameAndCountry(city.getName(), city.getCountry());
    }

    @Override
    public long getCityIdByNameAndCountry(String name, String country) {
        City city = cityRepository.findByNameAndCountry(name, country);

        if (city != null)
            return city.getId();

        return -1;
    }

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
    public City getCityFromPlace(String cityText, Double lat, Double lng) {

        City city = parsePlace(cityText, lat, lng);
        if (getCityIdByNameAndCountry(city.getName(), city.getCountry()) == -1)
            saveNewCity(city);

        return city;
    }

    private City parsePlace(String cityText, Double lat, Double lng) {
        String[] cityArray = cityText.split(",");
        String name = cityArray[0].trim();
        String country = cityArray[cityArray.length - 1].trim();

        City city = new City(name, country, lat, lng);

        return city;
    }
}
