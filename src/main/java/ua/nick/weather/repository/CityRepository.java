package ua.nick.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nick.weather.model.City;

public interface CityRepository extends JpaRepository<City, Long> {
    City findById(Long id);
    City findByName(String name);
    City findByNameAndCountry(String name, String country);
}