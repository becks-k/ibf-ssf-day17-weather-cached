package ibf.ssf.day17.weather.cached.repository;

import java.time.Duration;
import java.util.Optional;

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
        String _key = normalizeKey(key);
        // Set key to expire in 30 mins
        template.opsForValue().setIfAbsent(_key, value, Duration.ofMinutes(30));
    }

    // Get city data
    public Optional<String> getData(String key) {
        String _key = normalizeKey(key);
        String value = template.opsForValue().get(_key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    // Check if key exits
    public Boolean hasCity(String key) {
        String _key = normalizeKey(key);
        return template.hasKey(_key);
    }

    // Normalize key
    public String normalizeKey(String key) {
        return key.trim().toLowerCase().replaceAll("\\s+", "");
    }

}
