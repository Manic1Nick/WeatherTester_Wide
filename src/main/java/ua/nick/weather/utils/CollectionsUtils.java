package ua.nick.weather.utils;

import ua.nick.weather.model.AverageDiff;
import ua.nick.weather.model.Forecast;
import ua.nick.weather.model.Provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionsUtils {

    public static Map<Provider, Long> createMapForecastsCount(Map<Provider, List<Forecast>> mapAllForecasts) {
        Map<Provider, Long> forecastsCountByProviders = new HashMap<>();

        for (Provider provider : mapAllForecasts.keySet())
            forecastsCountByProviders.put(provider, (long) mapAllForecasts.get(provider).size());

        return forecastsCountByProviders;
    }

    public static Map<Provider, Long> createMapActualsCount(List<Forecast> listActuals) {

        return listActuals.stream()
                .collect(Collectors.groupingBy(Forecast::getProvider, Collectors.counting()));
    }

    public static List<AverageDiff> sortList(List<AverageDiff> list) {
        list.sort((diff1, diff2) -> (int) diff1.getValue() - (int) diff2.getValue());
        return list;
    }
}
