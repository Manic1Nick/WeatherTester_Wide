package ua.nick.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.nick.weather.model.*;
import ua.nick.weather.modelTester.TesterAverage;
import ua.nick.weather.modelTester.TesterItem;
import ua.nick.weather.service.UserService;
import ua.nick.weather.service.WeatherService;
import ua.nick.weather.utils.CollectionsUtils;
import ua.nick.weather.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ApplicationController {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(@ModelAttribute("message") String message, Model model) {

        /*
        * For choice city for analyse weather we need identifier user by system information.
        * If user identified successful we can find forecasts by user city,
        * if user didn't identifier we need offer choice city for analyse weather.
        * */

        //todo userService - need method determineCity() by user or pc;
        City city = userService.determineCity();
        if (city != null) {
            model.addAttribute("cityid", city.getId());
            //if user with city already registered we get main.jsp with weather analysis
            return "main";
        }

        List<String> cities = userService.getAllCitiesNames();
        model.addAttribute("cities", cities);

        //if user with city unregistered we get choice city in welcome.jsp
        return "welcome";
    }

    @RequestMapping(value = "/place", method = RequestMethod.GET)
    public void getCity(@RequestParam("city") String cityText, @RequestParam("lat") Double lat,
                          @RequestParam("lng") Double lng, Model model, HttpServletResponse resp)
                            throws IOException {

        City city = userService.getCityFromPlace(cityText, lat, lng);
        Long cityid = userService.getCityIdByNameAndCountry(city.getName(), city.getCountry());
        if (cityid == -1)
            cityid = userService.saveNewCity(city);

        resp.getWriter().printf(cityid.toString());
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String getForecastsAnalyse(@RequestParam("cityid") long cityId,
                                      @ModelAttribute("message") String message, Model model) {

        model.addAttribute("city", userService.getCityById(cityId));

        LocalDate today = LocalDate.now();
        LocalDate dateStart = today.minusDays(7);
        LocalDate dateEnd = today.plusDays(7);

        //for the page "previous 7 days"
        model.addAttribute("listDiffs",
                weatherService.createListDiffsForPeriodByCityId(dateStart, today, cityId));
        model.addAttribute("datesPrev",
                weatherService.createListStringDatesOfPeriod(dateStart, today));

        //for the page "next 7 days"
        model.addAttribute("mapForecasts",
                weatherService.createMapProviderForecastsForPeriodByCityId(today, dateEnd, cityId));
        model.addAttribute("datesNext",
                weatherService.createListStringDatesOfPeriod(today, dateEnd));

        //for the page "rating providers"
        List<AverageDiff> averages = CollectionsUtils.sortList(
                weatherService.getAllAverageDiffsByCityId(cityId));
        model.addAttribute("listAverages", averages);

        model.addAttribute("message", message);

        return "main";
    }

    @RequestMapping(value = "/forecasts/get/new", method = RequestMethod.GET)
    public void getNewForecasts(@RequestParam("cityid") long cityId,
                                HttpServletResponse resp, HttpServletRequest req)
                                throws IOException {

        String message;
        try {
            /*
            * If we have forecasts for all dates we can't test or use application
            * because we can't update any forecasts or actual weather.
            * If we wan't test application again or re-update weather data in DB
            * we need delete last forecast for new update.
            * And application will be ready to use.
            * */
            if (req.getParameter("reupdate") != null)
                weatherService.deleteLastForecastsInDB(false, cityId);

            List<Forecast> listForecasts = weatherService.getAllNewForecasts(cityId);

            City city = userService.getCityById(cityId);
            message = StringUtils.createMessageAboutForecasts(
                    CollectionsUtils.createMapForecastsCount(listForecasts), city);

        } catch (Exception e) {
            message = e.getMessage();
        }
        resp.getWriter().print(message);
    }

    @RequestMapping(value = "/actuals/get/new", method = RequestMethod.GET)
    public void getNewActuals(@RequestParam("cityid") long cityId,
                              HttpServletResponse resp, HttpServletRequest req)
                              throws IOException {

        String message;
        try {
            /*
            * If we have forecasts for all dates we can't test or use application
            * because we can't update any forecasts or actual weather.
            * If we wan't test application again or re-update weather data in DB
            * we need delete last actual weather for new update.
            * And application will be ready to use.
            * */
            if (req.getParameter("reupdate") != null)
                weatherService.deleteLastForecastsInDB(true, cityId);

            List<Forecast> listActuals = weatherService.getAllNewActuals(cityId);

            City city = userService.getCityById(cityId);
            message = StringUtils.createMessageAboutActuals(
                    CollectionsUtils.createMapForecastsCount(listActuals), city);

        } catch (Exception e) {
            message = e.getMessage();
        }
        resp.getWriter().print(message);
    }

    @RequestMapping(value = "/change/day", method = RequestMethod.GET)
    public void changeDay(@RequestParam("date") String date,
                          @RequestParam("index") String index,
                          HttpServletResponse resp)
                                throws IOException {

        date = index != null ? StringUtils.changeDateByIndex(date, index) : date ;
        resp.getWriter().print(date);
    }

    @RequestMapping(value = "/forecasts/find/ids", method = RequestMethod.GET)
    public void findForecastIdsByDay(@RequestParam("cityid") long cityId,
                                     @RequestParam("date") String date,
                                    HttpServletRequest req, HttpServletResponse resp, Model model)
                                    throws IOException {
        try {
            String ids = weatherService.getListForecastIdsForDateByCityId(date, cityId).stream()
                    .collect(Collectors.joining(";"));
            resp.getWriter().print(ids);

        } catch (Exception e) {
            resp.getWriter().print(e.getMessage());
        }
    }

    @RequestMapping(value = {"/forecasts/show/day"}, method = RequestMethod.GET)
    public String showForecastsByDate(@RequestParam("cityid") long cityId,
                                      @RequestParam("date") String date,
                                      @RequestParam("ids") String ids,
                                      Model model) throws IOException {
        model.addAttribute("date", date);

        City city = userService.getCityById(cityId);
        model.addAttribute("city", city);

        List<TesterAverage> averageTesters = weatherService.createListAverageTesters(date, cityId);
        model.addAttribute("listAverageTester", averageTesters);

        Map<Provider, List<TesterItem>> mapItemTesters = weatherService.createMapItemTesters(ids);
        model.addAttribute("mapItemTester", mapItemTesters);

        return "day_tester";
    }

    @RequestMapping(value = {"/update/all/average_diff"}, method = RequestMethod.GET)
    public void updateAverageDiffForAllDays(@RequestParam("cityid") long cityId, HttpServletResponse resp)
            throws IOException {

        List<AverageDiff> allAverageDiff = weatherService.updateAverageDiffForAllDays(cityId);

        resp.getWriter().print(StringUtils.createMessageAboutUpdateAverageDiff(allAverageDiff));
    }

    //todo usable or delete
    /*@RequestMapping(value = {"/forecasts/get/all"}, method = RequestMethod.GET)
    public String getTotalAnalysis(@ModelAttribute("message") String message, Model model) {

        List<Provider> providers = new ArrayList<>();
        List<AverageDiff> averagesTotal = new ArrayList<>();
        Map<Provider, List<Integer>> mapTemps = new HashMap<>();
        for (Provider provider : Arrays.asList(Provider.values())) {
            providers.add(provider);
            averagesTotal.add(weatherService.getAverageDiff(provider));

            List<Forecast> forecasts = weatherService.getAllForecastsFromProvider(provider);
            forecasts.sort((o1, o2) -> o1.getId().intValue()-o2.getId().intValue());
            List<Integer> temps = new ArrayList<>();
            for (Forecast forecast : forecasts)
                temps.add((forecast.getTempMax() + forecast.getTempMin()) / 2);

            mapTemps.put(provider, temps);

        }
        model.addAttribute("providers", providers);
        model.addAttribute("averagesTotal", averagesTotal);
        model.addAttribute("temps1", mapTemps.get(Provider.OPENWEATHER));
        model.addAttribute("temps2", mapTemps.get(Provider.WUNDERGROUND));
        model.addAttribute("temps3", mapTemps.get(Provider.FORECA));

        model.addAttribute("items", new ArrayList<>(Constants.FIELDS_AND_RANGES_MAP.keySet()));
        model.addAttribute("values", weatherService.createListOfAverageItems());


        model.addAttribute("message", message);

        return "total_analysis";
    }*/
}
