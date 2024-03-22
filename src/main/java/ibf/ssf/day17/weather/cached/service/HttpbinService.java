package ibf.ssf.day17.weather.cached.service;

import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpbinService {
    
    // Network access
    // One way of checking liveness
    // Making API calls to weathermap would be costly, hence approach is to send a get request to..?
    public boolean isAlive() {

        RequestEntity<Void> req = RequestEntity
            .get("https://httpbin.org/get")
            .build();
        
        try {
            RestTemplate template = new RestTemplate();
            template.exchange(req, String.class);
            return true;

        } catch (Exception e) {
            return false;
        }
        
    }
}
