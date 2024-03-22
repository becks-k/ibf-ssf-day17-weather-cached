package ibf.ssf.day17.weather.cached.configuration;

import ibf.ssf.day17.weather.cached.utils.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Value("${spring.data.redis.database}")
    private Integer redisDatabase;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    // Factory method
    // Bean allows Autowire to inject properties into other files
    // Springboot has an existing redistemplate
    @Bean(Util.REDIS_ONE)
    public RedisTemplate<String, String> createRedis() {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(redisDatabase);

        // Set username and password if present
        if (redisUsername.trim().length() > 0) {
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        // Configure Jedis (driver)
        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();

        JedisConnectionFactory fac = new JedisConnectionFactory(config, jedisClient);
        fac.afterPropertiesSet();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(fac);

        // Why the need to serialize?
        // If a database stores information from many programming languages, it is able
        // to convert the information to a platform netural value for readibility.
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }
}
