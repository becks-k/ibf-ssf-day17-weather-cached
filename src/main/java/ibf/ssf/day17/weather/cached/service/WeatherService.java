package ibf.ssf.day17.weather.cached.service;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ibf.ssf.day17.weather.cached.model.Weather;
import ibf.ssf.day17.weather.cached.repository.WeatherRepo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class WeatherService {
    
    @Autowired
    WeatherRepo weatherRepo;

    // Establish base url
    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    @Value("${weather.api.key}")
    private String apiKey;

    public List<Weather> search(String term) {
        
        // Build url with query parameters
        String url = UriComponentsBuilder
        .fromUriString(BASE_URL)
        .queryParam("q", term.replaceAll(" ", "+")) // If keying in hong kong, space turns into another char hence replace space with +
        .queryParam("appid", apiKey)
        .queryParam("units", "metric")
        .toUriString();

        // Build get RequestEntity
        RequestEntity<Void> req = RequestEntity
        .get(url)
        .accept(MediaType.APPLICATION_JSON)
        .build();

        RestTemplate template = new RestTemplate();
        
        // Get results of response
        ResponseEntity<String> resp; 
        try {
            resp = template.exchange(req, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Returns an empty list for th:if and th:unless conditions in thymeleaf
            return List.of();
        }

        // DEBUG
        System.out.printf(">>>>>> Status code: %d\n", resp.getStatusCode().value());
        System.out.printf(">>>>>> Payload: %s\n", resp.getBody());

        // Process the body
        List<Weather> weatherList = new LinkedList<>();
        

        JsonReader reader = Json.createReader(new StringReader(resp.getBody()));
        JsonObject result = reader.readObject();
        JsonArray weather = result.getJsonArray("weather");
        for (int i = 0; i < weather.size(); i++) {
            JsonObject elem = weather.getJsonObject(i);
            String main = elem.getString("main");
            String description = elem.getString("description");
            String icon = elem.getString("icon");
            
            Weather weatherResult = new Weather(icon, main, description);
            weatherList.add(weatherResult);

        }

        // // Alternative
        // weather.getJsonArray("weather").stream()
        // .map(value -> value.asJsonObject())
        // .map(j -> new Weather(j.getString("main")), j.getString("description"), j.getString("icon"));

        return weatherList;

    }


    // Create weather data
    public void cacheWeatherData(String city, List<Weather> weather) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Weather w : weather) {
            arrayBuilder.add(w.toString());
        }
        weatherRepo.createData(city, arrayBuilder.build().toString());
    }

    // Get weather data
    // Data is stored as JsonObject String
    public List<Weather> getWeatherData(String city) {
        String rawWeather = weatherRepo.getData(city).get();
        JsonReader reader = Json.createReader(new StringReader(rawWeather));
        JsonArray arr = reader.readArray();
        List<Weather> weatherList = arr.stream()
            .map(v -> v.asJsonObject())
            .map(v -> {
                Weather weather = new Weather(v.getString("icon"), v.getString("main"), v.getString("description"));
                return weather; 
            })
            .toList();

        return weatherList;
    }

    // Check if weather key exists
    public Boolean hasCity(String key) {
        return weatherRepo.hasCity(key);
    }

    
}
