package ua.nick.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nick.weather.model.City;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findAll();
    City findById(Long id);
    City findByName(String name);
    City findByNameAndCountry(String name, String country);
    City findByLatAndLon(double lat, double lon);
}