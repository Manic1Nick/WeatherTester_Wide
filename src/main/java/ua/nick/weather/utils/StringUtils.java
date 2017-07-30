package ua.nick.weather.utils;

import ua.nick.weather.model.AverageDiff;
import ua.nick.weather.model.City;
import ua.nick.weather.model.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {

    public static String changeDateByIndex(String date, String index) {
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        if (date == null) {
            date = LocalDateTime.now().format(yyyyMMdd);

        } else {
            LocalDate localDate = LocalDate.parse(date, yyyyMMdd);

            if (index != null)
                localDate = Integer.valueOf(index) == 1 ? localDate.plusDays(1)
                        : Integer.valueOf(index) == -1 ? localDate.minusDays(1)
                        : localDate;

            date = localDate.format(yyyyMMdd);
        }
        return date;
    }

    public static String createMessageAboutUpdateAverageDiff(List<AverageDiff> list) {

        if (list != null && list.size() > 0) {
            //providers updated -> days updated
            Map<Provider, Integer> mapCounts = list.stream()
                    .filter(avDiff -> avDiff != null)
                    .collect(Collectors.toMap(AverageDiff::getProvider, AverageDiff::getDays));

            int size = mapCounts.keySet().size();
            if (size > 0) {
                String countedByProviders = mapCounts.keySet().stream()
                        .map(provider -> "</br>" + mapCounts.get(provider) + " from " + provider)
                        .collect(Collectors.joining());

                return String.format("New %s average differences were updated:%s", size, countedByProviders);
            }
        }
        return "There is no need to update average differences for any providers.";
    }

    public static String createMessageAboutForecasts(Map<Provider, Long> map, City city) {

        if (map == null || map.keySet().isEmpty())
            return createNoUpdatedMessage(city);

        String baseMessage = String.format(createBaseUpdatedMessage(map, city), "forecast(s)");
        String addingMessage = createAddingMessage(map);
        String errorMessage = createErrorUpdatedMessage(map);

        return baseMessage + addingMessage + errorMessage;
    }

    public static String createMessageAboutActuals(Map<Provider, Long> map, City city) {

        if (map == null || map.keySet().isEmpty())
            return createNoUpdatedMessage(city);

        String baseMessage = String.format(createBaseUpdatedMessage(map, city), "actual weather(s)");
        String errorMessage = createErrorUpdatedMessage(map);

        return baseMessage + errorMessage;
    }

    private static String createBaseUpdatedMessage(Map<Provider, Long> map, City city) {

        Long total = map.values().stream()
                .filter(count -> count != null)
                .reduce((n1, n2) -> n1 + n2).orElse(0L);

        String countedByProviders = map.keySet().stream()
                .map(provider -> "</br>" + map.get(provider) + " from " + provider)
                .collect(Collectors.joining());

        return String.format("New %s %s for %s were added to database:%s",
                total, //quantity items
                "%s", //forecasts or actuals
                city.textNameCountry(), //city
                countedByProviders); //list forecasts
    }

    private static String createAddingMessage(Map<Provider, Long> map) {
        //add message about providers with expanded json
        String addingMessage = "";
        for (Provider provider : map.keySet())
            if (provider.hasExpandedJson())
                addingMessage += "</br> 1 actual weather from " + provider;

        if (!addingMessage.isEmpty())
            addingMessage = "</br></br>Additionally added:" + addingMessage;

        return addingMessage;
    }

    private static String createNoUpdatedMessage(City city) {
        return String.format("There is no need to update forecasts for %s from providers for this date. " +
                "</br>Try tomorrow or re-update data.", city.textNameCountry());
    }

    private static String createErrorUpdatedMessage(Map<Provider, Long> map) {

        if (map.keySet().size() >= Provider.lengthWithoutExpandedJson())
            return "";

        String listErrorUpdated = "";
        for (Provider provider : Provider.values())
            if (!map.keySet().contains(provider))
                listErrorUpdated += "<br/>" + provider.getName();

        return String.format("</br></br>No forecasts from provider(s):%s<br/>Try connect later",
                listErrorUpdated);
    }
}
