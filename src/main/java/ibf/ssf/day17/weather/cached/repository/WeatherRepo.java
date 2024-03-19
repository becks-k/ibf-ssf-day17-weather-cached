package ibf.ssf.day17.weather.cached.repository;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import ibf.ssf.day17.weather.cached.utils.Util;

@Repository
public class WeatherRepo {
    
    @Autowired
    @Qualifier(Util.REDIS_ONE)
    RedisTemplate<String, String> template;

    // Store city data
    public void createData(String key, String value) {
        // Set key to expire in 30 mins
        template.opsForValue().setIfAbsent(key, value, Duration.ofMinutes(30));
    }

    // Get city data
    public String getData(String key) {
        return template.opsForValue().get(key);
    }

    // Check if key exits
    public Boolean hasCity(String key) {
        return template.hasKey(key);
    }

}
