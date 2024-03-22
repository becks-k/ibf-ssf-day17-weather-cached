package ibf.ssf.day17.weather.cached.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ibf.ssf.day17.weather.cached.model.Weather;
import ibf.ssf.day17.weather.cached.service.HttpbinService;
import ibf.ssf.day17.weather.cached.service.WeatherService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
@RequestMapping
public class WeatherController {
    
    @Autowired
    WeatherService weatherService;

    @Autowired
    HttpbinService httpbinService;

    @GetMapping("/weather")
    public ModelAndView getWeather(@RequestParam String q) {
        
        List<Weather> results = new LinkedList<>();

        if (weatherService.hasCity(q)) {
            // Get weather from cache
            results = weatherService.getWeatherData(q);
        } else {
            // Get weather from OWM
            results = weatherService.search(q);

            if (!results.isEmpty()) {
                // Cache weather (30 mins)
                weatherService.cacheWeatherData(q, results);
            }
        }
        

        ModelAndView mav = new ModelAndView("weather");

        mav.setStatus(HttpStatusCode.valueOf(200));
        mav.addObject("q", q);
        mav.addObject("results", results);

        return mav;
    }

    // Return JsonObject if alive
    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<String> getHealth() {

        // Create a json object
        JsonObject jO = Json.createObjectBuilder()
            .build();

        if (httpbinService.isAlive()) {
            // Return status ok and jObject to string if alive
            return ResponseEntity.ok(jO.toString());
        }
        // Else return status 400 with jO object to string
        return ResponseEntity.status(400).body(jO.toString());
    }
}
