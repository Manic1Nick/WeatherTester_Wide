package ua.nick.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nick.weather.model.Diff;
import ua.nick.weather.model.Provider;

import java.util.List;

public interface DiffRepository extends JpaRepository<Diff, Long> {
    List<Diff> findByDate(String date);
    List<Diff> findByProvider(Provider provider);
    Diff findByDateAndProvider(String date, Provider provider);

    List<Diff> findByDateAndCityId(String date, long cityId);
    List<Diff> findByProviderAndCityId(Provider provider, long cityId);
    Diff findByDateAndProviderAndCityId(String date, Provider provider, long cityId);
}