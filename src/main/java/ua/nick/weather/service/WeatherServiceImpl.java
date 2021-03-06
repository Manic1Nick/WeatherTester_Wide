package ua.nick.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.nick.weather.exception.ForecastNotFoundInDBException;
import ua.nick.weather.model.*;
import ua.nick.weather.modelTester.TesterAverage;
import ua.nick.weather.modelTester.TesterItem;
import ua.nick.weather.repository.AverageDiffRepository;
import ua.nick.weather.repository.CityRepository;
import ua.nick.weather.repository.DiffRepository;
import ua.nick.weather.repository.ForecastRepository;
import ua.nick.weather.utils.NetUtils;
import ua.nick.weather.weatherFactory.ActualWeatherFactory;
import ua.nick.weather.weatherFactory.DiffsFactory;
import ua.nick.weather.weatherFactory.ForecastFactory;
import ua.nick.weather.weatherFactory.LinkForecastsFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service(value = "weatherService")
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    private ForecastRepository forecastRepository;
    @Autowired
    private DiffRepository diffRepository;
    @Autowired
    private AverageDiffRepository averageDiffRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ForecastFactory forecastFactory;
    @Autowired
    private ActualWeatherFactory actualFactory;
    @Autowired
    private DiffsFactory diffsFactory;
    @Autowired
    private LinkForecastsFactory linkForecastsFactory;

    @Override
    public List<Forecast> getAllNewForecasts(long cityId) {
        List<Forecast> allForecasts = new ArrayList<>();

        for (Provider provider : Provider.values())
            if (needUpdateForecasts(provider, cityId)) {
                List<Forecast> list = getNewForecastsFromProvider(provider, cityId);

                if (!list.isEmpty() && !provider.hasExpandedJson())
                    list = saveListNewForecasts(list);

                allForecasts.addAll(list);
            }
        return allForecasts;
    }

    @Override
    public List<Forecast> getAllNewActuals(long cityId)
            throws IOException, URISyntaxException, ParseException {
        List<Forecast> actuals = new ArrayList<>();

        for (Provider provider : Provider.values()) {
            if (needUpdateActuals(provider, cityId)) {
                Forecast actual = getActualWeatherFromProviderByCityId(provider, cityId);

                actuals.add(actual);
                saveNewForecast(actual);

                createAndSaveNewDiff(actual, cityId);
            }
        }
        return actuals;
    }

    @Override
    public List<String> getListForecastIdsForDateByCityId(String date, long cityId)
            throws ForecastNotFoundInDBException {
        Map<Provider, List<Forecast>> mapByProviders = getAllForecastsByDateAndCityId(date, cityId)
                .stream().collect(Collectors.groupingBy(Forecast::getProvider));

        //create text line of ids pairs "forecast,actual" ("1,2;3,4;...)
        List<String> listIds = createListOfPairIds(mapByProviders);

        if (listIds.isEmpty()) {
            String exceptionMessage = createTemplateNoForecastsInDB(date, cityId);
            throw new ForecastNotFoundInDBException(String.format(exceptionMessage,
                    "any forecasts or any actuals weather"));
        }
        return listIds;
    }

    @Override
    public Map<Provider, List<Forecast>> getListForecastsForDateByCityId(String date, long cityId)
            throws ForecastNotFoundInDBException {
        Map<Provider, List<Forecast>> mapByProviders = getAllForecastsByDateAndCityId(date, cityId)
                .stream().collect(Collectors.groupingBy(Forecast::getProvider));

        String exceptionMessage = createTemplateNoForecastsInDB(date, cityId);
        if (mapByProviders == null || mapByProviders.isEmpty())
            throw new ForecastNotFoundInDBException(String.format(exceptionMessage,
                    "any forecasts or any actuals weather"));

        return mapByProviders;
    }

    @Override
    public void saveNewDiff(Diff diff) {
        diffRepository.save(diff);
    }

    @Override
    public List<AverageDiff> getAllAverageDiffsByCityId(long cityId) {
        return averageDiffRepository.findAllByCityId(cityId);
    }

    @Override
    public List<AverageDiff> updateAverageDiffForAllDays(long cityId) {
        List<AverageDiff> allAverageDiff = new ArrayList<>();

        setZeroForAllAverageDiff(cityId); // ATTENTION !!!

        for (Provider provider : Provider.values()) {
            List<Diff> listDiffs = diffRepository.findByProviderAndCityId(provider, cityId);

            if (listDiffs != null && !listDiffs.isEmpty())
                for (Diff diff : listDiffs)
                    allAverageDiff.add(createAverageDiff(diff, cityId));
        }
        return allAverageDiff;
    }

    @Override
    public List<TesterAverage> createListAverageTesters(String date, long cityId) {

        return Arrays.asList(Provider.values()).stream()
                .map(p -> createTesterAverage(date, p, cityId))
                .filter(tester -> tester != null)
                .sorted((t1, t2) -> (int) Double.parseDouble(t1.getValueDay()) - (int) Double.parseDouble(t2.getValueDay()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Provider, List<TesterItem>> createMapItemTesters(String ids) {
        Map<Provider, List<TesterItem>> mapItemTesters = new HashMap<>();

        for (String pair : ids.split(";")) {
            String[] pairIds = pair.split(",");
            Long idForecast = Long.parseLong(pairIds[0]);
            Long idActual = Long.parseLong(pairIds[1]);
            Provider provider = forecastRepository.findById(idForecast).getProvider();

            mapItemTesters.put(provider, createDayListItemTestersByIds(idForecast, idActual));
        }
        return mapItemTesters;
    }

    @Override
    public List<Diff> createListDiffsForPeriodByCityId(LocalDate from, LocalDate to, long cityId) {

        return createListDatesOfPeriod(from, to).stream()
                .map(date -> findBestDiffForDate(date, cityId))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Provider, List<Forecast>> createMapProviderForecastsForPeriodByCityId(
                                                LocalDate from, LocalDate to, long cityId) {
        Map<Provider, List<Forecast>> mapForecasts = new HashMap<>();

        for (Provider provider : Provider.values())
            mapForecasts.put(provider, getForecastsByProviderForPeriodByCityId(provider, from, to, cityId));

        return mapForecasts;
    }

    @Override
    public List<String> createListStringDatesOfPeriod(LocalDate from, LocalDate to) {

        return createListDatesOfPeriod(from, to).stream()
                .map(DateTimeFormatter.ofPattern("yyyy/MM/dd")::format)
                .collect(Collectors.toList());
    }

    @Override
    public List<Forecast> deleteLastForecastsInDB(boolean actual, long cityId) {
        List<Forecast> lastForecasts = new ArrayList<>();

        for (Provider provider : Provider.values()) {
            List<Forecast> forecasts = getAllForecastsByProviderAndCityId(provider, actual, cityId);
            Forecast lastForecast;

            if (!forecasts.isEmpty()) {
                lastForecast = forecasts.get(forecasts.size() - 1);
                deleteForecastInDB(lastForecast);
                lastForecasts.add(lastForecast);
            }
        }
        return lastForecasts;
    }

    //todo usable or delete
    /*@Override
    public List<Integer> createListOfAverageItems() {
        List<Diff> allDiffs = diffRepository.findAll();

        List<Integer> averageItems = new ArrayList<>();
        averageItems.add((int) allDiffs.stream().mapToDouble(Diff::getTempDiff).average().getAsDouble());
        averageItems.add((int) allDiffs.stream().mapToDouble(Diff::getPressureDiff).average().getAsDouble());
        averageItems.add((int) allDiffs.stream().mapToDouble(Diff::getCloudsDiff).average().getAsDouble());
        averageItems.add((int) allDiffs.stream().mapToDouble(Diff::getWindSpeedDiff).average().getAsDouble());
        averageItems.add((int) allDiffs.stream().mapToDouble(Diff::getDescriptionDiff).average().getAsDouble());

        return averageItems;
    }*/

    private void saveNewForecast(Forecast forecast) {
        forecastRepository.save(forecast);
    }

    private Forecast getForecastById(Long id) {
        return forecastRepository.findById(id);
    }

    private List<Forecast> getAllForecastsByProviderAndCityId(Provider provider, boolean actual, long cityId) {
        return forecastRepository.findByProviderAndActualAndCityId(provider, actual, cityId);
    }

    private void deleteForecastInDB(Forecast forecast) {
        forecastRepository.delete(forecast);
    }

    private List<Forecast> getAllForecastsByDateAndCityId(String date, long cityId)
            throws ForecastNotFoundInDBException {

        List<Forecast> allDateForecasts = forecastRepository.findByDateAndCityId(date, cityId);
        if (allDateForecasts == null || allDateForecasts.isEmpty())
            throw new ForecastNotFoundInDBException(
                    String.format(createTemplateNoForecastsInDB(date, cityId),
                            "any forecasts"));

        return allDateForecasts;
    }

    private Forecast getActualWeatherFromProviderByCityId(Provider provider, long cityId)
            throws URISyntaxException, IOException, ParseException {

        City city = getCityById(cityId);
        String link = createLinkFromProviderByCity(provider, city, true); //actual weather? = true

        URI uri = new URI(link);
        URL url = uri.toURL();

        String json = NetUtils.urlToString(url);

        return actualFactory.createActualModelFromJson(provider, cityId, json);
    }

    private String createTemplateNoForecastsInDB(String date, long cityId) {
        City city = getCityById(cityId);

        return String.format("There are no %s for %s from %s in DB. " +
                        "Please update database for this date before analysis.",
                "%s", //what was not found in DB (forecast, or actual weather, ot both)
                city.textNameCountry(), //city,country
                date); //date
    }

    private List<Forecast> getNewForecastsFromProvider(Provider provider, long cityId) {
        List<Forecast> forecasts = new ArrayList<>();

        try {
            City city = getCityById(cityId);
            String link = createLinkFromProviderByCity(provider, city, false); //actual weather? = false

            URI uri = new URI(link);
            URL url = uri.toURL();
            String json = NetUtils.urlToString(url);

            //some providers have 1 json for all forecasts and actual (and has limit for free connection)
            if (provider.hasExpandedJson())
                return createListForecastsForExpandedJson(provider, cityId, json);

            forecasts = forecastFactory.createListForecastsFromJsonByProvider(provider, cityId, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return forecasts;
    }

    private City getCityById(long cityId) {
        return cityRepository.findById(cityId);
    }

    private String createLinkFromProviderByCity(Provider provider, City city, boolean actual) {
        return linkForecastsFactory.createLinkForecastsForProviderByCity(provider, city, actual);
    }

    private List<Forecast> createListForecastsForExpandedJson(Provider provider, long cityId, String json) {
        List<Forecast> forecasts = new ArrayList<>();

        try {
            forecasts = forecastFactory.createListForecastsFromJsonByProvider(provider, cityId, json);
            forecasts = saveListNewForecasts(forecasts);

            if (needUpdateActuals(provider, cityId)) {
                Forecast actual = actualFactory.createActualModelFromJson(provider, cityId, json);
                saveNewForecast(actual);
                createAndSaveNewDiff(actual, cityId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return forecasts;
    }

    private List<Forecast> saveListNewForecasts(List<Forecast> list) {

        list = validateNewForecasts(list);
        for (Forecast forecast : list)
            saveNewForecast(forecast);

        return list;
    }

    private List<String> createListOfPairIds(Map<Provider, List<Forecast>> mapByProviders) {
        List<String> listIds = new ArrayList<>();

        for (List<Forecast> list : mapByProviders.values()) {
            if (list.size() == 2) {
                list.sort((f1, f2) -> Boolean.compare(f1.isActual(), f2.isActual()));

                listIds.add(String.format("%s,%s;",
                        list.get(0).getId().toString(),
                        list.get(1).getId().toString()));
            }
        }
        return listIds;
    }

    private TesterAverage createTesterAverage(String date, Provider provider, long cityId) {
        TesterAverage testerAverage = null;

        Diff diff = diffRepository.findByDateAndProviderAndCityId(date, provider, cityId);

        if (diff != null) {
            AverageDiff averageDiff = averageDiffRepository.findByProviderAndCityId(provider, cityId);

            testerAverage = new TesterAverage(
                    provider,
                    date,
                    String.valueOf(diff.getAverageDayDiff()),
                    String.valueOf(averageDiff.getValue()),
                    String.valueOf(averageDiff.getDays())
            );
        }

        return testerAverage;
    }

    private List<Forecast> getForecastsByProviderForPeriodByCityId(
                                Provider provider, LocalDate from, LocalDate to, long cityId) {

        return createListStringDatesOfPeriod(from, to).stream()
                .map(date -> forecastRepository.findByDateAndProviderAndActualAndCityId(
                        date, provider, false, cityId))
                .filter(forecast -> forecast != null)
                .sorted((forecast1, forecast2) -> forecast1.getId().intValue() - forecast2.getId().intValue())
                .collect(Collectors.toList());
    }

    private Diff findBestDiffForDate(LocalDate localDate, long cityId) {
        Diff minDiff = null;

        String date = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        List<Diff> currentDateDiffs = diffRepository.findByDateAndCityId(date, cityId);

        if (currentDateDiffs != null && !currentDateDiffs.isEmpty())
            minDiff = currentDateDiffs.stream()
                    .min(Comparator.comparingDouble(Diff::getAverageDayDiff))
                    .get();

        return minDiff;
    }

    private List<LocalDate> createListDatesOfPeriod(LocalDate from, LocalDate to) {

        return Stream.iterate(from.plusDays(1), day -> day.plusDays(1))
                .limit(Period.between(from, to).getDays())
                .collect(Collectors.toList());
    }

    private List<TesterItem> createDayListItemTestersByIds(Long idForecast, Long idActual) {

        Forecast forecast = forecastRepository.findById(idForecast);
        Forecast actual = forecastRepository.findById(idActual);
        Diff diff = diffRepository.findByDateAndProviderAndCityId(
                actual.getDate(), actual.getProvider(), actual.getCityId());

        return Constants.FIELDS_AND_RANGES_MAP.keySet().stream()
                .map((name) -> new TesterItem(
                        name,
                        actual.getProvider(),
                        actual.getDate(),
                        forecast.getTextValueByString(name),
                        actual.getTextValueByString(name),
                        diff.getTextValueByString(name)
                )).collect(Collectors.toList());
    }

    private Diff createAndSaveNewDiff(Forecast actual, long cityId) {
        Diff diff = checkActualInDB(actual) ?
                calculateDiff(actual) :
                null ;

        if (diff != null && !checkDiffInDB(diff)) {
            diff.setInclInAverageDiff(true);
            diff.setCityId(cityId);

            saveNewDiff(diff);

            createAverageDiff(diff, cityId);
        }
        return diff;
    }

    private Diff calculateDiff(Forecast actual) {
        Forecast forecast = forecastRepository.findByDateAndProviderAndActualAndCityId(
                actual.getDate(), actual.getProvider(), false, actual.getCityId());
        if (forecast == null)
                return null;

        return diffsFactory.createNewDiff(forecast, actual);
    }

    private AverageDiff createAverageDiff(Diff diff, long cityId) {
        AverageDiff averageDiff = averageDiffRepository.findByProviderAndCityId(diff.getProvider(), cityId);

        if (averageDiff == null)
            averageDiff = new AverageDiff(diff.getProvider(), cityId, 1,
                    diff.getTempDiff(), diff.getPressureDiff(), diff.getCloudsDiff(),
                    diff.getWindSpeedDiff(), diff.getDescriptionDiff(), diff.getAverageDayDiff());
        else
            averageDiff = diffsFactory.addDiffToAverageDiff(averageDiff, diff);

        return saveAverageDiff(averageDiff);
    }

    private void setZeroForAllAverageDiff(long cityId) {
        List<AverageDiff> listAll = averageDiffRepository.findAllByCityId(cityId);

        if (listAll != null && !listAll.isEmpty()) {
            for (AverageDiff diff : listAll) {
                diff.setValue(0.0);
                diff.setDays(0);
                diff.setCityId(cityId);

                saveAverageDiff(diff);
            }
        }
    }

    private AverageDiff saveAverageDiff(AverageDiff avDiff) {
        return averageDiffRepository.save(avDiff);
    }

    //clear list forecast for better save
    private List<Forecast> validateNewForecasts(List<Forecast> list) {
        List<Forecast> listNewForecasts = new ArrayList<>();

        for (Forecast forecast : list)
            if (!forecast.isActual() && !checkForecastInDB(forecast))
                listNewForecasts.add(forecast);
            else if (forecast.isActual() && !checkActualInDB(forecast))
                listNewForecasts.add(forecast);

        return listNewForecasts;
    }

    private boolean checkForecastInDB(Forecast forecast) {
        return forecastRepository.findByDateAndProviderAndActualAndCityId(
                forecast.getDate(), forecast.getProvider(), false, forecast.getCityId()) != null;
    }

    private boolean checkActualInDB(Forecast actual) {
        return forecastRepository.findByDateAndProviderAndActualAndCityId(
                actual.getDate(), actual.getProvider(), true, actual.getCityId()) != null;
    }

    private boolean checkDiffInDB(Diff diff) {
        return diffRepository.findByDateAndProviderAndCityId(
                diff.getDate(), diff.getProvider(), diff.getCityId()) != null;
    }

    private boolean needUpdateForecasts(Provider provider, long cityId) {
        int maxDays = provider.getMaxDaysForecast();

        for (int i = 1; i < maxDays; i++) {
            long addingDays = i * 86400000; //86400000 is 24 hours
            String testDate = new java.text.SimpleDateFormat("yyyy/MM/dd")
                    .format(System.currentTimeMillis() + addingDays);

            if (forecastRepository.findByDateAndProviderAndActualAndCityId(
                    testDate, provider, false, cityId) == null)
                return true;
        }
        return false;
    }

    private boolean needUpdateActuals(Provider provider, long cityId) {
        String today = new java.text.SimpleDateFormat("yyyy/MM/dd")
                .format(System.currentTimeMillis());

        return forecastRepository.findByDateAndProviderAndActualAndCityId(
                today, provider, true, cityId) == null;
    }

    /*//test
    public static void main(String[] args) {

        ParseUtils parseUtils = new ParseUtils();
        Provider provider = Provider.FORECA;

        try {
            URI uri = new URI(provider.getLinkForecast());
            URL url = uri.toURL();
            String json = NetUtils.urlToString(url);
            System.out.println("FORECAST: " + json);

            *//*List<Forecast> forecasts = parseUtils.createListForecastModelsFromJson(provider, json);
            System.out.println("FORECASTS:");
            forecasts.stream().forEach(System.out::println);*//*

            uri = new URI(provider.getLinkActual());
            url = uri.toURL();
            json = NetUtils.urlToString(url);
            System.out.println("CURRENT: " + json);

            *//*Forecast forecast = parseUtils.createActualModelFromJson(provider, json);
            System.out.println("DATE " + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + " ACTUAL:");
            System.out.println(forecast);*//*

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
}
