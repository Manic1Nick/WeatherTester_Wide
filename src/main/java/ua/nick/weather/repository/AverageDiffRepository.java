package ua.nick.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nick.weather.model.AverageDiff;
import ua.nick.weather.model.Provider;

import java.util.List;

public interface AverageDiffRepository extends JpaRepository<AverageDiff, Long> {
    AverageDiff findByProvider(Provider provider);
    List<AverageDiff> findAllByCityId(long cityId);

    AverageDiff findByProviderAndCityId(Provider provider, long cityId);
}