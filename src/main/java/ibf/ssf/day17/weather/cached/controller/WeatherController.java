package ibf.ssf.day17.weather.cached.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ibf.ssf.day17.weather.cached.model.Weather;
import ibf.ssf.day17.weather.cached.service.WeatherService;

@Controller
@RequestMapping
public class WeatherController {
    
    @Autowired
    WeatherService weatherService;

    @GetMapping("/weather")
    public ModelAndView getWeather(@RequestParam String q) {
        
        List<Weather> results = new LinkedList<>();

        if (weatherService.hasCity(q)) {
            // Get weather from cache
            results = weatherService.getWeatherData(q);
        } else {
            // Get weather from OWM
            results = weatherService.search(q);
        }
        
        if (!results.isEmpty()) {
            // Cache weather (30 mins)
            weatherService.createWeatherData(q, results.get(0));
        }

        ModelAndView mav = new ModelAndView("weather");

        mav.setStatus(HttpStatusCode.valueOf(200));
        mav.addObject("q", q);
        mav.addObject("results", results);

        return mav;
    }
}
