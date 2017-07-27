package ua.nick.weather.service;

import ua.nick.weather.exception.ForecastNotFoundInDBException;
import ua.nick.weather.exception.NoDataFromProviderException;
import ua.nick.weather.model.AverageDiff;
import ua.nick.weather.model.Diff;
import ua.nick.weather.model.Forecast;
import ua.nick.weather.model.Provider;
import ua.nick.weather.modelTester.TesterAverage;
import ua.nick.weather.modelTester.TesterItem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeatherService {

    void saveNewForecast(Forecast forecast);
    Forecast getForecastById(Long id);

    List<Forecast> getAllNewForecasts(long cityId)
            throws IOException, URISyntaxException, ParseException, NoDataFromProviderException;
    List<Forecast> getAllNewActuals(long cityId)
            throws IOException, URISyntaxException, ParseException;
    /*Forecast getActualWeatherFromProviderByCityId(Provider provider, long cityId)
            throws URISyntaxException, IOException, ParseException;*/
    List<String> getListForecastIdsForDateByCityId(String date, long cityId)
            throws ForecastNotFoundInDBException;

    void saveNewDiff(Diff diff);
    List<AverageDiff> getAllAverageDiffsByCityId(long cityId);
    //List<Integer> createListOfAverageItems();
    List<TesterAverage> createListAverageTesters(String date, long cityId);
    Map<Provider, List<TesterItem>> createMapItemTesters(String ids);
    List<Diff> createListDiffsForPeriodByCityId(LocalDate from, LocalDate to, long cityId);
    Map<Provider, List<Forecast>> createMapProviderForecastsForPeriodByCityId(LocalDate from, LocalDate to, long cityId);
    List<String> createListStringDatesOfPeriod(LocalDate from, LocalDate to);

    List<Forecast> deleteLastForecastsInDB(boolean actual, long cityId);

    //admin
    List<AverageDiff> updateAverageDiffForAllDays(long cityId);
}
