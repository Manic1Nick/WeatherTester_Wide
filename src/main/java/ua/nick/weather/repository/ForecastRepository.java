package ua.nick.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nick.weather.model.Forecast;
import ua.nick.weather.model.Provider;

import java.util.List;

public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    Forecast findById(Long id);
    List<Forecast> findByDate(String date);
    List<Forecast> findByProviderAndActual(Provider provider, boolean actual);
    Forecast findByDateAndProviderAndActual(String date, Provider provider, boolean actual);
    void delete(Forecast forecast);

    List<Forecast> findByDateAndCityId(String date, long cityId);
    List<Forecast> findByProviderAndActualAndCityId(Provider provider, boolean actual, long cityId);
    Forecast findByDateAndProviderAndActualAndCityId(String date, Provider provider, boolean actual, long cityId);
}